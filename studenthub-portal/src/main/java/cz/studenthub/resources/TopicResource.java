/*******************************************************************************
 *     Copyright (C) 2017  Stefan Bunciak
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package cz.studenthub.resources;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import cz.studenthub.core.Topic;
import cz.studenthub.core.TopicApplication;
import cz.studenthub.core.User;
import cz.studenthub.core.UserRole;
import cz.studenthub.db.TopicApplicationDAO;
import cz.studenthub.db.TopicDAO;
import cz.studenthub.util.PagingUtil;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.IntParam;
import io.dropwizard.jersey.params.LongParam;

@Path("/topics")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TopicResource {

  private final TopicDAO topicDao;
  private final TopicApplicationDAO appDao;

  public TopicResource(TopicDAO topicDao, TopicApplicationDAO taDao) {
    this.topicDao = topicDao;
    this.appDao = taDao;
  }

  @GET
  @UnitOfWork
  public List<Topic> fetch(@Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
      @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam) {
    return PagingUtil.paging(topicDao.findAll(), startParam.get(), sizeParam.get());
  }

  @GET
  @Path("/{id}")
  @UnitOfWork
  public Topic findById(@PathParam("id") LongParam id) {
    return topicDao.findById(id.get());
  }

  @DELETE
  @Path("/{id}")
  @UnitOfWork
  @RolesAllowed("ADMIN")
  public Response delete(@PathParam("id") LongParam idParam) {
    Long id = idParam.get();
    Topic topic = topicDao.findById(id);
    if (topic == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    topicDao.delete(topic);
    return Response.noContent().build();
  }

  @PUT
  @Path("/{id}")
  @UnitOfWork
  @RolesAllowed("TECH_LEADER")
  public Response update(@PathParam("id") LongParam idParam, @NotNull @Valid Topic topic, @Auth User user) {

    Long id = idParam.get();
    Topic oldTopic = topicDao.findById(id);
    if (oldTopic == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    Long oldCreatorId = oldTopic.getCreator().getId();
    Long newCreatorId = topic.getCreator().getId();
    // if user is topic creator and creator stays the same, or is an admin
    if ((oldCreatorId.equals(user.getId()) && oldCreatorId.equals(newCreatorId))
        || user.getRoles().contains(UserRole.ADMIN)) {

      topic.setId(id);
      topicDao.update(topic);
      return Response.ok(topic).build();
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @POST
  @UnitOfWork
  @RolesAllowed("TECH_LEADER")
  public Response create(@NotNull @Valid Topic topic, @Auth User user) {

    // If user is topic creator or is an admin
    if (topic.getCreator().equals(user) || user.getRoles().contains(UserRole.ADMIN)) {

      topicDao.create(topic);
      if (topic.getId() == null)
        throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);

      return Response.created(UriBuilder.fromResource(TopicResource.class).path("/{id}").build(topic.getId()))
          .entity(topic).build();
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @PUT
  @Path("/{id}/supervise")
  @UnitOfWork
  @RolesAllowed("AC_SUPERVISOR")
  public Response superviseTopic(@PathParam("id") LongParam id, @Auth User supervisor) {
    Topic topic = topicDao.findById(id.get());
    topic.getAcademicSupervisors().add(supervisor);
    topicDao.update(topic);
    return Response.ok(topic).build();
  }

  @GET
  @Path("/{id}/applications")
  @UnitOfWork
  @PermitAll
  public List<TopicApplication> fetchSupervisedTopics(@PathParam("id") LongParam id,
      @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
      @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam) {

    Topic topic = topicDao.findById(id.get());
    if (topic == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    return PagingUtil.paging(appDao.findByTopic(topic), startParam.get(), sizeParam.get());
  }

  @GET
  @Path("/{id}/supervisors")
  @UnitOfWork
  @PermitAll
  public List<User> getTopicSupervisors(@PathParam("id") LongParam id,
      @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
      @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam) {

    Topic topic = topicDao.findById(id.get());
    return PagingUtil.paging(topic.getAcademicSupervisors(), startParam.get(), sizeParam.get());
  }

  @GET
  @Path("/{id}/creator")
  @UnitOfWork
  @PermitAll
  public User getTopicCreator(@PathParam("id") LongParam id) {
    Topic topic = topicDao.findById(id.get());
    return topic.getCreator();
  }

  @GET
  @Path("/search")
  @UnitOfWork
  public List<Topic> search(@NotNull @QueryParam("text") String text,
      @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
      @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam) {
    return PagingUtil.paging(topicDao.search(text), startParam.get(), sizeParam.get());
  }

}
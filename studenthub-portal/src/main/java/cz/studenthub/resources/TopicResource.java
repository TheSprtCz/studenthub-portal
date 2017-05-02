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

import static cz.studenthub.auth.Consts.ADMIN;
import static cz.studenthub.auth.Consts.AUTHENTICATED;
import static cz.studenthub.auth.Consts.BASIC_AUTH;
import static cz.studenthub.auth.Consts.JWT_AUTH;
import static cz.studenthub.auth.Consts.SUPERVISOR;
import static cz.studenthub.auth.Consts.TECH_LEADER;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;

import cz.studenthub.auth.StudentHubProfile;
import cz.studenthub.core.Topic;
import cz.studenthub.core.TopicApplication;
import cz.studenthub.core.User;
import cz.studenthub.core.UserRole;
import cz.studenthub.db.TopicApplicationDAO;
import cz.studenthub.db.TopicDAO;
import cz.studenthub.db.UserDAO;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.IntParam;
import io.dropwizard.jersey.params.LongParam;

@Path("/topics")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Pac4JSecurity(authorizers = ADMIN, clients = { BASIC_AUTH, JWT_AUTH })
public class TopicResource {

  private final UserDAO userDao;
  private final TopicDAO topicDao;
  private final TopicApplicationDAO appDao;

  public TopicResource(TopicDAO topicDao, TopicApplicationDAO taDao, UserDAO userDao) {
    this.userDao = userDao;
    this.topicDao = topicDao;
    this.appDao = taDao;
  }

  @GET
  @UnitOfWork
  @Pac4JSecurity(ignore = true)
  public List<Topic> fetch(@Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
      @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam) {
    return paging(topicDao.findAll(), startParam.get(), sizeParam.get());
  }

  @GET
  @Path("/{id}")
  @UnitOfWork
  @Pac4JSecurity(ignore = true)
  public Topic findById(@PathParam("id") LongParam id) {
    return topicDao.findById(id.get());
  }

  @DELETE
  @Path("/{id}")
  @UnitOfWork
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
  @Pac4JSecurity(authorizers = TECH_LEADER, clients = { BASIC_AUTH, JWT_AUTH })
  public Response update(@Pac4JProfile StudentHubProfile profile, @PathParam("id") LongParam idParam,
      @NotNull @Valid Topic topic) {

    Long id = idParam.get();
    Topic oldTopic = topicDao.findById(id);
    if (oldTopic == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    Long oldCreatorId = oldTopic.getCreator().getId();
    Long newCreatorId = topic.getCreator().getId();
    // if user is topic creator and creator stays the same, or is an admin
    if ((oldCreatorId.equals(Long.valueOf(profile.getId())) && oldCreatorId.equals(newCreatorId))
        || profile.getRoles().contains(UserRole.ADMIN.name())) {

      topic.setId(id);
      topicDao.update(topic);
      return Response.ok(topic).build();
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @POST
  @UnitOfWork
  @Pac4JSecurity(authorizers = TECH_LEADER, clients = { BASIC_AUTH, JWT_AUTH })
  public Response create(@Pac4JProfile StudentHubProfile profile, @NotNull @Valid Topic topic) {

    // If user is topic creator or is an admin
    if (topic.getCreator().getId().equals(Long.valueOf(profile.getId()))
        || profile.getRoles().contains(UserRole.ADMIN.name())) {
	
	    topicDao.create(topic);
	    if (topic.getId() == null)
	      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
	
	    return Response.created(UriBuilder.fromResource(TopicResource.class).path("/{id}").build(topic.getId()))
	        .entity(topic).build();
    }
    else {
        throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @PUT
  @Path("/{id}/supervise")
  @UnitOfWork
  @Pac4JSecurity(authorizers = SUPERVISOR, clients = { BASIC_AUTH, JWT_AUTH })
  public Response superviseTopic(@Pac4JProfile StudentHubProfile profile, @PathParam("id") LongParam id) {
    Topic topic = topicDao.findById(id.get());
    User supervisor = userDao.findById(Long.valueOf(profile.getId()));
    topic.getAcademicSupervisors().add(supervisor);
    topicDao.update(topic);
    return Response.ok(topic).build();
  }

  @GET
  @Path("/{id}/applications")
  @UnitOfWork
  @Pac4JSecurity(authorizers = AUTHENTICATED, clients = { BASIC_AUTH, JWT_AUTH })
  public List<TopicApplication> fetchSupervisedTopics(@PathParam("id") LongParam id) {
    Topic topic = topicDao.findById(id.get());
    if (topic == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    return appDao.findByTopic(topic);
  }

  /*
   * We don't want to publicly expose info about creator/supervisors we need
   * separate endpoints
   */
  @GET
  @Path("/{id}/supervisors")
  @UnitOfWork
  @Pac4JSecurity(authorizers = AUTHENTICATED, clients = { BASIC_AUTH, JWT_AUTH })
  public Set<User> getTopicSupervisors(@PathParam("id") LongParam id) {
    Topic topic = topicDao.findById(id.get());
    return topic.getAcademicSupervisors();
  }

  @GET
  @Path("/{id}/creator")
  @UnitOfWork
  @Pac4JSecurity(authorizers = AUTHENTICATED, clients = { BASIC_AUTH, JWT_AUTH })
  public User getTopicCreator(@PathParam("id") LongParam id) {
    Topic topic = topicDao.findById(id.get());
    return topic.getCreator();
  }

  @GET
  @Path("/search")
  @UnitOfWork
  @Pac4JSecurity(ignore = true)
  public List<Topic> search(@NotNull @QueryParam("text") String text,
      @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
      @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam) {
    return paging(topicDao.search(text), startParam.get(), sizeParam.get());
  }

  public List<Topic> paging(List<Topic> list, int start, int size) {
    ArrayList<Topic> topics = new ArrayList<Topic>(list);
    int topicSize = topics.size();
    if (start > topicSize)
      start = 0;

    int remaining = topicSize - start;

    if (size > remaining || size == 0)
      size = remaining;

    return topics.subList(start, start + size);
  }
}
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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import cz.studenthub.api.UpdateUserBean;
import cz.studenthub.core.Topic;
import cz.studenthub.core.TopicApplication;
import cz.studenthub.core.User;
import cz.studenthub.core.UserRole;
import cz.studenthub.db.TopicApplicationDAO;
import cz.studenthub.db.TopicDAO;
import cz.studenthub.db.UserDAO;
import cz.studenthub.util.PagingUtil;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.IntParam;
import io.dropwizard.jersey.params.LongParam;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

  private final UserDAO userDao;
  private final TopicApplicationDAO appDao;
  private final TopicDAO topicDao;

  // private static final Logger LOG =
  // LoggerFactory.getLogger(UserResource.class);

  public UserResource(UserDAO userDao, TopicDAO topicDao, TopicApplicationDAO taDao) {
    this.userDao = userDao;
    this.appDao = taDao;
    this.topicDao = topicDao;
  }

  @GET
  @UnitOfWork
  @RolesAllowed("ADMIN")
  public List<User> fetch(@Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
      @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam) {
    return PagingUtil.paging(userDao.findAll(), startParam.get(), sizeParam.get());
  }

  @GET
  @Path("/{id}")
  @UnitOfWork
  @PermitAll
  public User findById(@Auth User user, @PathParam("id") LongParam id) {
    // only admin or profile owner is allowed
    if (id.get().equals(user.getId()) || user.getRoles().contains(UserRole.ADMIN)) {
      return userDao.findById(id.get());
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @DELETE
  @Path("/{id}")
  @UnitOfWork
  public Response delete(@Auth User user, @PathParam("id") LongParam idParam) {
    Long id = idParam.get();

    // only admin or profile owner is allowed
    if (id.equals(user.getId()) || user.getRoles().contains(UserRole.ADMIN)) {
      userDao.delete(user);
      return Response.noContent().build();
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @PUT
  @Path("/{id}")
  @UnitOfWork
  public Response update(@PathParam("id") LongParam idParam, @NotNull @Valid UpdateUserBean updateUserBean,
      @Auth User user) {
    
    Long id = idParam.get();

    if (id.equals(user.getId()) || user.getRoles().contains(UserRole.ADMIN)) {
      user.setName(updateUserBean.getName());
      user.setEmail(updateUserBean.getEmail());
      user.setFaculty(updateUserBean.getFaculty());
      user.setCompany(updateUserBean.getCompany());
      user.setPhone(updateUserBean.getPhone());
      user.setRoles(updateUserBean.getRoles());
      user.setTags(updateUserBean.getTags());
      userDao.update(user);
      return Response.ok(user).build();
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @GET
  @Path("/{id}/applications")
  @UnitOfWork
  @RolesAllowed("STUDENT")
  public List<TopicApplication> fetchApplications(@Auth User user, @PathParam("id") LongParam id,
      @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
      @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam) {

    if (id.get().equals(user.getId()) || user.getRoles().contains(UserRole.ADMIN)) {
      User student = userDao.findById(id.get());
      return PagingUtil.paging(appDao.findByStudent(student), startParam.get(), sizeParam.get());
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @GET
  @Path("/{id}/leadApplications")
  @UnitOfWork
  @RolesAllowed("TECH_LEADER")
  public List<TopicApplication> fetchLeadApps(@Auth User user, @PathParam("id") LongParam id,
      @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
      @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam) {

    if (id.get().equals(user.getId()) || user.getRoles().contains(UserRole.ADMIN)) {
      User leader = userDao.findById(id.get());
      return PagingUtil.paging(appDao.findByLeader(leader), startParam.get(), sizeParam.get());
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @GET
  @Path("/{id}/ownedTopics")
  @UnitOfWork
  @RolesAllowed("TECH_LEADER")
  public List<Topic> fetchOwnedTopics(@Auth User user, @PathParam("id") LongParam id,
      @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
      @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam) {

    if (id.get().equals(user.getId()) || user.getRoles().contains(UserRole.ADMIN)) {
      User creator = userDao.findById(id.get());
      return PagingUtil.paging(topicDao.findByCreator(creator), startParam.get(), sizeParam.get());
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @GET
  @Path("/{id}/supervisedTopics")
  @UnitOfWork
  @RolesAllowed("AC_SUPERVISOR")
  public List<Topic> fetchSupervisedTopics(@Auth User user, @PathParam("id") LongParam id,
      @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
      @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam) {

    if (id.get().equals(user.getId()) || user.getRoles().contains(UserRole.ADMIN)) {
      User supervisor = userDao.findById(id.get());
      return PagingUtil.paging(topicDao.findBySupervisor(supervisor), startParam.get(), sizeParam.get());
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @GET
  @Path("/{id}/supervisedApplications")
  @UnitOfWork
  @RolesAllowed("AC_SUPERVISOR")
  public List<TopicApplication> fetchSupervisedApplications(@Auth User user, @PathParam("id") LongParam id,
      @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
      @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam) {

    if (id.get().equals(user.getId()) || user.getRoles().contains(UserRole.ADMIN)) {
      User supervisor = userDao.findById(id.get());
      return PagingUtil.paging(appDao.findBySupervisor(supervisor), startParam.get(), sizeParam.get());
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }
}

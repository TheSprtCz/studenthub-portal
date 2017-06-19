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
package net.thesishub.resources;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.IntParam;
import io.dropwizard.jersey.params.LongParam;
import net.thesishub.core.Notification;
import net.thesishub.core.Project;
import net.thesishub.core.Topic;
import net.thesishub.core.TopicApplication;
import net.thesishub.core.User;
import net.thesishub.db.NotificationDAO;
import net.thesishub.db.ProjectDAO;
import net.thesishub.db.TopicApplicationDAO;
import net.thesishub.db.TopicDAO;
import net.thesishub.db.UserDAO;
import net.thesishub.util.Equals;
import net.thesishub.util.PagingUtil;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

  @Inject
  private UserDAO userDao;

  @Inject
  private TopicApplicationDAO appDao;

  @Inject
  private TopicDAO topicDao;

  @Inject
  private ProjectDAO projectDao;

  @Inject
  private NotificationDAO notifDao;

  @GET
  @Timed
  @UnitOfWork
  @RolesAllowed("ADMIN")
  public List<User> fetch(@Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
      @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
      @Context HttpServletResponse response) {
    return PagingUtil.paging(userDao.findAll(), startParam.get(), sizeParam.get(), response);
  }

  @GET
  @Path("/{id}")
  @UnitOfWork
  @PermitAll
  public User findById(@Auth User user, @PathParam("id") LongParam idParam) {
    Long id = idParam.get();

    // only admin or profile owner is allowed
    if (Equals.id(user, id) || user.isAdmin()) {
      return userDao.findById(id);
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @DELETE
  @ExceptionMetered
  @Path("/{id}")
  @UnitOfWork
  public Response delete(@Auth User authUser, @PathParam("id") LongParam idParam) {
    Long id = idParam.get();
    User user = userDao.findById(id);

    // only admin or profile owner is allowed
    if (Equals.id(authUser, id) || authUser.isAdmin()) {
      userDao.delete(user);
      return Response.noContent().build();
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @PUT
  @ExceptionMetered
  @Path("/{id}")
  @UnitOfWork
  public Response update(@PathParam("id") LongParam idParam, @NotNull @Valid User user,
      @Auth User authUser) {
    
    Long id = idParam.get();
    User oldUser = userDao.findById(id);

    // If user is updating himself and doesn't change his roles or is admin
    if ((Equals.id(authUser, id) && oldUser.getRoles().equals(user.getRoles())
        && Equals.id(oldUser.getFaculty(), user.getFaculty())
        && Equals.id(oldUser.getCompany(), user.getCompany())) || authUser.isAdmin()) {
      user.setId(id);
      user.setPassword(oldUser.getPassword());
      userDao.update(user);
      return Response.ok(user).build();
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @GET
  @Timed
  @Path("/{id}/applications")
  @UnitOfWork
  @RolesAllowed({"ADMIN", "STUDENT"})
  public List<TopicApplication> fetchApplications(@Auth User user, @PathParam("id") LongParam idParam,
      @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
      @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
      @Context HttpServletResponse response) {

    Long id = idParam.get();
    if (Equals.id(user, id) || user.isAdmin()) {
      User student = userDao.findById(id);
      return PagingUtil.paging(appDao.findByStudent(student), startParam.get(), sizeParam.get(), response);
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @GET
  @Timed
  @Path("/{id}/ledApplications")
  @UnitOfWork
  @RolesAllowed({"ADMIN", "TECH_LEADER"})
  public List<TopicApplication> fetchLedApps(@Auth User user, @PathParam("id") LongParam idParam,
      @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
      @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
      @Context HttpServletResponse response) {

    Long id = idParam.get();
    if (Equals.id(user, id) || user.isAdmin()) {
      User leader = userDao.findById(id);
      return PagingUtil.paging(appDao.findByLeader(leader), startParam.get(), sizeParam.get(), response);
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @GET
  @Timed
  @Path("/{id}/ownedTopics")
  @UnitOfWork
  @RolesAllowed({"ADMIN", "TECH_LEADER"})
  public List<Topic> fetchOwnedTopics(@Auth User user, @PathParam("id") LongParam idParam,
      @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
      @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
      @Context HttpServletResponse response) {

    Long id = idParam.get();
    if (Equals.id(user, id) || user.isAdmin()) {
      User creator = userDao.findById(id);
      return PagingUtil.paging(topicDao.findByCreator(creator), startParam.get(), sizeParam.get(), response);
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @GET
  @Timed
  @Path("/{id}/supervisedTopics")
  @UnitOfWork
  @RolesAllowed({"ADMIN", "AC_SUPERVISOR"})
  public List<Topic> fetchSupervisedTopics(@Auth User user, @PathParam("id") LongParam idParam,
      @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
      @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
      @Context HttpServletResponse response) {

    Long id = idParam.get();
    if (Equals.id(user, id) || user.isAdmin()) {
      User supervisor = userDao.findById(id);
      return PagingUtil.paging(topicDao.findBySupervisor(supervisor), startParam.get(), sizeParam.get(), response);
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @GET
  @Timed
  @Path("/{id}/supervisedApplications")
  @UnitOfWork
  @RolesAllowed({"ADMIN", "AC_SUPERVISOR"})
  public List<TopicApplication> fetchSupervisedApplications(@Auth User user, @PathParam("id") LongParam idParam,
      @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
      @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
      @Context HttpServletResponse response) {

    Long id = idParam.get();
    if (Equals.id(user, id) || user.isAdmin()) {
      User supervisor = userDao.findById(id);
      return PagingUtil.paging(appDao.findBySupervisor(supervisor), startParam.get(), sizeParam.get(), response);
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @GET
  @Timed
  @Path("/{id}/projects")
  @UnitOfWork
  @RolesAllowed({"ADMIN", "PROJECT_LEADER"})
  public List<Project> fetchProjects(@Auth User user, @PathParam("id") LongParam idParam,
      @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
      @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
      @Context HttpServletResponse response) {

    Long id = idParam.get();
    if (Equals.id(user, id) || user.isAdmin()) {
      User creator = userDao.findById(id);
      return PagingUtil.paging(projectDao.findByCreator(creator), startParam.get(), sizeParam.get(), response);
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @GET
  @Timed
  @Path("/notifications")
  @UnitOfWork
  @PermitAll
  public List<Notification> fetchNotifications(@Auth User user,@PathParam("id") LongParam idParam,
      @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
      @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
      @Context HttpServletResponse response) {

    return PagingUtil.paging(notifDao.findByUser(user), startParam.get(), sizeParam.get(), response);
  }
}

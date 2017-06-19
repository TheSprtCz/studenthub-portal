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
import java.util.Set;

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
import javax.ws.rs.POST;
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
import javax.ws.rs.core.UriBuilder;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.IntParam;
import io.dropwizard.jersey.params.LongParam;
import io.dropwizard.validation.Validated;
import net.thesishub.core.ApplicationStatus;
import net.thesishub.core.Task;
import net.thesishub.core.TopicApplication;
import net.thesishub.core.User;
import net.thesishub.db.TaskDAO;
import net.thesishub.db.TopicApplicationDAO;
import net.thesishub.util.NotificationUtil;
import net.thesishub.util.PagingUtil;
import net.thesishub.util.Equals;
import net.thesishub.validators.groups.CreateUpdateChecks;

@Path("/applications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TopicApplicationResource {

  @Inject
  private TopicApplicationDAO appDao;

  @Inject
  private TaskDAO taskDao;

  @Inject
  private NotificationUtil notifUtil;

  @GET
  @Timed
  @UnitOfWork
  @PermitAll
  public List<TopicApplication> fetch(@Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
          @Context HttpServletResponse response) {
    return PagingUtil.paging(appDao.findAll(), startParam.get(), sizeParam.get(), response);
  }

  @GET
  @Path("/{id}")
  @UnitOfWork
  @PermitAll
  public TopicApplication findById(@PathParam("id") LongParam id) {
    return appDao.findById(id.get());
  }

  @DELETE
  @ExceptionMetered
  @Path("/{id}")
  @UnitOfWork
  @RolesAllowed("ADMIN")
  public Response delete(@PathParam("id") LongParam idParam) {
    Long id = idParam.get();
    TopicApplication app = appDao.findById(id);
    if (app == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    appDao.delete(app);
    return Response.noContent().build();
  }

  @PUT
  @ExceptionMetered
  @Path("/{id}")
  @UnitOfWork
  @RolesAllowed({ "ADMIN", "AC_SUPERVISOR", "STUDENT", "TECH_LEADER" })
  public Response update(@PathParam("id") LongParam idParam, @NotNull @Valid @Validated(CreateUpdateChecks.class) TopicApplication app, @Auth User user) throws JsonProcessingException {
    
    long id = idParam.get();
    TopicApplication oldApp = appDao.findById(id);
    if (oldApp == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    // allow topic creator/leader, assigned student, topic supervisor, admin
    if (Equals.id(oldApp.getTechLeader(), user)
        || (Equals.id(oldApp.getStudent(), user) && oldApp.getStatus() == app.getStatus())
        || Equals.id(oldApp.getAcademicSupervisor(), user) 
        || user.isAdmin()) {
      app.setId(id);
      appDao.update(app);

      if (oldApp.getGrade() == null && app.getGrade() != null) {
        notifUtil.applicationGraded(oldApp, app, user);
      } else if (oldApp.getStatus() != app.getStatus()) {
        notifUtil.statusChanged(oldApp, app, user);
      } else {
        notifUtil.applicationUpdated(oldApp, app, user);
      }

      return Response.ok(app).build();
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @POST
  @ExceptionMetered
  @UnitOfWork
  @RolesAllowed("STUDENT")
  public Response create(@NotNull @Valid @Validated(CreateUpdateChecks.class) TopicApplication app, @Auth User user) throws JsonProcessingException {

    // Student can only create applications for himself.
    if ((Equals.id(app.getStudent(), user) && app.getStatus() == ApplicationStatus.WAITING_APPROVAL) || user.isAdmin()) {
      appDao.create(app);
      if (app.getId() == null)
        throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
  
      notifUtil.applicationCreated(app);
      return Response.created(UriBuilder.fromResource(TopicApplicationResource.class).path("/{id}").build(app.getId()))
          .entity(app).build();
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @POST
  @Path("/massDecline")
  @ExceptionMetered
  @RolesAllowed({ "ADMIN", "AC_SUPERVISOR", "TECH_LEADER" })
  public Response massDecline(@NotNull Set<TopicApplication> applications, @Auth User user) {
    for (TopicApplication app : applications) {
      Long id = app.getId();
      if (id == null)
        throw new WebApplicationException(Status.NOT_FOUND);

      // UnitOfWork supports only one transaction so it can't update multiple
      // entities so I had to create workaround, perform works the same as
      // UnitOfWotk but can handle multiple transactions
      appDao.perform(() -> {
        // Fetch TopicApplication from DB
        TopicApplication fetched = appDao.findById(id);
        if (fetched == null)
          throw new WebApplicationException(Status.NOT_FOUND);

        // Only application that are waiting for approval can be mass declined
        if (fetched.getStatus() != ApplicationStatus.WAITING_APPROVAL) 
          throw new WebApplicationException(Status.NOT_ACCEPTABLE);

        // Only Admin, TechLeader and Supervisor can mass decline
        if (user.isAdmin() || (fetched.getTechLeader() != null && fetched.getTechLeader().equals(user))
            || (fetched.getAcademicSupervisor() != null && fetched.getAcademicSupervisor().equals(user))) {
          fetched.setStatus(ApplicationStatus.DECLINED);

          appDao.update(fetched);

          //System.out.println(appDao.findById(id).getStatus());
        } else {
          throw new WebApplicationException(Status.FORBIDDEN);
        }
      });
    }
    return Response.ok().build();
  }
  /*
   * Task endpoints
   */

  @GET
  @Timed
  @Path("/{id}/tasks")
  @UnitOfWork
  @RolesAllowed({ "ADMIN", "AC_SUPERVISOR", "STUDENT", "TECH_LEADER" })
  public List<Task> getTasksByApplication(@PathParam("id") LongParam id, @Auth User user,
          @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
          @Context HttpServletResponse response) {

    TopicApplication app = appDao.findById(id.get());
    // allow only app student, leader and/or supervisor
    if (TaskResource.isAllowedToAccessTask(app, user)) {
      return PagingUtil.paging(taskDao.findByTopicApplication(app), startParam.get(), sizeParam.get(), response);
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

}

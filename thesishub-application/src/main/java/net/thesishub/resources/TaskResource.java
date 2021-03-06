/*******************************************************************************
 *     Copyright (C) 2017  Petr Hála
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

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import com.codahale.metrics.annotation.ExceptionMetered;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;
import net.thesishub.core.Task;
import net.thesishub.core.TopicApplication;
import net.thesishub.core.User;
import net.thesishub.db.TaskDAO;
import net.thesishub.db.TopicApplicationDAO;
import net.thesishub.util.Equals;

@Path("/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll
public class TaskResource {

  @Inject
  private TopicApplicationDAO appDao;

  @Inject
  private TaskDAO taskDao;

  @GET
  @Path("/{id}")
  @UnitOfWork
  public Task getTaskById(@PathParam("id") LongParam id) {
    return taskDao.findById(id.get());
  }

  @POST
  @ExceptionMetered
  @UnitOfWork
  @RolesAllowed({ "ADMIN", "AC_SUPERVISOR", "STUDENT", "TECH_LEADER" })
  public Response createTask(@NotNull @Valid Task task, @Auth User user) {

    TopicApplication app = appDao.findById(task.getApplication().getId());
    // allow only app student, leader and/or supervisor
    if (!isAllowedToAccessTask(app, user))
      throw new WebApplicationException(Status.FORBIDDEN);

    task.setApplication(app);
    taskDao.create(task);

    if (task.getId() == null)
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);

    return Response.created(UriBuilder.fromResource(TaskResource.class).path("/{id}").build(task.getId())).entity(task)
        .build();
  }

  @PUT
  @ExceptionMetered
  @Path("/{id}")
  @UnitOfWork
  @RolesAllowed({ "ADMIN", "AC_SUPERVISOR", "STUDENT", "TECH_LEADER" })
  public Response updateTask(@PathParam("id") LongParam taskId, @NotNull @Valid Task task, @Auth User user) {

    TopicApplication app = appDao.findById(task.getApplication().getId());
    // allow only app student, leader and/or supervisor
    if (!isAllowedToAccessTask(app, user))
      throw new WebApplicationException(Status.FORBIDDEN);

    task.setId(taskId.get());
    taskDao.update(task);
    return Response.ok(task).build();
  }

  @DELETE
  @ExceptionMetered
  @Path("/{id}")
  @UnitOfWork
  @RolesAllowed({ "ADMIN", "AC_SUPERVISOR", "STUDENT", "TECH_LEADER" })
  public Response deleteTask(@PathParam("id") LongParam taskId, @Auth User user) {

    Task task = taskDao.findById(taskId.get());
    TopicApplication app = task.getApplication();

    // allow only app student, leader and/or supervisor
    if (!isAllowedToAccessTask(app, user))
      throw new WebApplicationException(Status.FORBIDDEN);

    taskDao.delete(task);
    return Response.noContent().build();
  }

  public static boolean isAllowedToAccessTask(TopicApplication app, User user) {
    return (Equals.id(app.getAcademicSupervisor(), user)) 
        || Equals.id(app.getStudent(), user)
        || Equals.id(app.getTechLeader(), user)
        || user.isAdmin();
  }

}
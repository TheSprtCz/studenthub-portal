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
package cz.studenthub.resources;

import static cz.studenthub.auth.Consts.AUTHENTICATED;
import static cz.studenthub.auth.Consts.BASIC_AUTH;
import static cz.studenthub.auth.Consts.JWT_AUTH;
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

import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;

import cz.studenthub.auth.StudentHubProfile;
import cz.studenthub.core.Task;
import cz.studenthub.core.TopicApplication;
import cz.studenthub.core.UserRole;
import cz.studenthub.db.TaskDAO;
import cz.studenthub.db.TopicApplicationDAO;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;

@Path("/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Pac4JSecurity(authorizers = AUTHENTICATED, clients = { BASIC_AUTH, JWT_AUTH })
public class TaskResource {

  private final TopicApplicationDAO appDao;
  private final TaskDAO taskDao;

  public TaskResource(TopicApplicationDAO appDao, TaskDAO taskDao) {
    this.appDao = appDao;
    this.taskDao = taskDao;
  }

  @GET
  @Path("/{id}")
  @UnitOfWork
  public Task getTaskById(@PathParam("id") LongParam id) {
    return taskDao.findById(id.get());
  }

  @POST
  @UnitOfWork
  public Response createTask(@NotNull @Valid Task task,
      @Pac4JProfile StudentHubProfile profile) {

    TopicApplication app = appDao.findById(task.getApplication().getId());
    // allow only app student, leader and/or supervisor
    if (!isAllowedToAccessTask(app, profile))
      throw new WebApplicationException(Status.FORBIDDEN);

    task.setApplication(app);
    taskDao.create(task);

    if (task.getId() == null)
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);

    return Response.created(
        UriBuilder.fromResource(TaskResource.class).path("/{id}").build(task.getId()))
        .entity(task).build();
  }

  @PUT
  @Path("/{id}")
  @UnitOfWork
  public Response updateTask(@PathParam("id") LongParam taskId,
      @NotNull @Valid Task task, @Pac4JProfile StudentHubProfile profile) {

    TopicApplication app = appDao.findById(task.getApplication().getId());

    // allow only app student, leader and/or supervisor
    if (!isAllowedToAccessTask(app, profile))
      throw new WebApplicationException(Status.FORBIDDEN);

    task.setId(taskId.get());
    taskDao.update(task);
    return Response.ok(task).build();
  }

  @DELETE
  @Path("/{id}")
  @UnitOfWork
  public Response deleteTask(@PathParam("id") LongParam taskId,
      @Pac4JProfile StudentHubProfile profile) {

    Task task = taskDao.findById(taskId.get());
    TopicApplication app = task.getApplication();

    // allow only app student, leader and/or supervisor
    if (!isAllowedToAccessTask(app, profile))
      throw new WebApplicationException(Status.FORBIDDEN);

    taskDao.delete(task);
    return Response.noContent().build();
  }

  public static boolean isAllowedToAccessTask(TopicApplication app, StudentHubProfile profile) {
    Long profileId = Long.valueOf(profile.getId());
    return app.getAcademicSupervisor().getId().equals(profileId) || app.getStudent().getId().equals(profileId)
        || app.getTechLeader().getId().equals(profileId) || profile.getRoles().contains(UserRole.ADMIN.name());
  }

}

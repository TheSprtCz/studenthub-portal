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

import cz.studenthub.core.Task;
import cz.studenthub.core.TopicApplication;
import cz.studenthub.core.User;
import cz.studenthub.core.UserRole;
import cz.studenthub.db.TaskDAO;
import cz.studenthub.db.TopicApplicationDAO;
import cz.studenthub.util.PagingUtil;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.IntParam;
import io.dropwizard.jersey.params.LongParam;

@Path("/applications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TopicApplicationResource {

  private final TopicApplicationDAO appDao;
  private final TaskDAO taskDao;

  public TopicApplicationResource(TopicApplicationDAO appDao, TaskDAO taskDao) {
    this.appDao = appDao;
    this.taskDao = taskDao;
  }

  @GET
  @UnitOfWork
  @PermitAll
  public List<TopicApplication> fetch(@Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam) {
    return PagingUtil.paging(appDao.findAll(), startParam.get(), sizeParam.get());
  }

  @GET
  @Path("/{id}")
  @UnitOfWork
  @PermitAll
  public TopicApplication findById(@PathParam("id") LongParam id) {
    return appDao.findById(id.get());
  }

  @DELETE
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
  @Path("/{id}")
  @UnitOfWork
  public Response update(@Auth User user, @PathParam("id") LongParam idParam, @NotNull @Valid TopicApplication app) {
    
    long id = idParam.get();
    TopicApplication oldApp = appDao.findById(id);
    if (oldApp == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    // allow topic creator/leader, assigned student, topic supervisor, admin
    if (oldApp.getTechLeader().equals(user) || oldApp.getStudent().equals(user)
        || oldApp.getAcademicSupervisor().equals(user) || user.getRoles().contains(UserRole.ADMIN)) {
      app.setId(id);
      appDao.update(app);
      return Response.ok(app).build();
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @POST
  @UnitOfWork
  @RolesAllowed("STUDENT")
  public Response create(@NotNull @Valid TopicApplication app) {
    appDao.create(app);
    if (app.getId() == null)
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);

    // TODO: notify topic creator and supervisor(s)

    return Response.created(UriBuilder.fromResource(TopicApplicationResource.class).path("/{id}").build(app.getId()))
        .entity(app).build();
  }

  /*
   * Task endpoints
   */

  @GET
  @Path("/{id}/tasks")
  @UnitOfWork
  public List<Task> getTasksByApplication(@PathParam("id") LongParam id, @Auth User user,
          @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam) {

    TopicApplication app = appDao.findById(id.get());
    // allow only app student, leader and/or supervisor
    if (TaskResource.isAllowedToAccessTask(app, user)) {
      return PagingUtil.paging(taskDao.findByTopicApplication(app), startParam.get(), sizeParam.get());
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

}

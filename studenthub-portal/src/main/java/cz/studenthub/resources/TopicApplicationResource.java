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
import static cz.studenthub.auth.Consts.STUDENT;

import java.util.List;

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
import cz.studenthub.core.TopicApplication;
import cz.studenthub.core.UserRole;
import cz.studenthub.db.TopicApplicationDAO;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;

@Path("/applications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Pac4JSecurity(authorizers = AUTHENTICATED, clients = { BASIC_AUTH, JWT_AUTH })
public class TopicApplicationResource {

  private final TopicApplicationDAO appDao;

  public TopicApplicationResource(TopicApplicationDAO appDao) {
    this.appDao = appDao;
  }

  @GET
  @UnitOfWork
  @Pac4JSecurity(authorizers = ADMIN, clients = { BASIC_AUTH, JWT_AUTH })
  public List<TopicApplication> fetch() {
    return appDao.findAll();
  }

  @GET
  @Path("/{id}")
  @UnitOfWork
  public TopicApplication findById(@PathParam("id") LongParam id) {
    return appDao.findById(id.get());
  }

  @DELETE
  @Path("/{id}")
  @UnitOfWork
  @Pac4JSecurity(authorizers = ADMIN, clients = { BASIC_AUTH, JWT_AUTH })
  public Response delete(@PathParam("id") LongParam id) {
    appDao.delete(appDao.findById(id.get()));
    return Response.noContent().build();
  }

  @PUT
  @Path("/{id}")
  @UnitOfWork
  public Response update(@Pac4JProfile StudentHubProfile profile, @PathParam("id") LongParam id,
      @NotNull @Valid TopicApplication app) {
    // allow topic creator/leader, assigned student, topic supervisor, admin
    Long userId = Long.valueOf(profile.getId());
    if (app.getTechLeader().getId().equals(userId) || app.getStudent().getId().equals(userId)
        || app.getAcademicSupervisor().getId().equals(userId) || profile.getRoles().contains(UserRole.ADMIN.name())) {
      app.setId(id.get());
      appDao.createOrUpdate(app);
      return Response.ok(app).build();
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @POST
  @UnitOfWork
  @Pac4JSecurity(authorizers = STUDENT, clients = { BASIC_AUTH, JWT_AUTH })
  public Response create(@NotNull @Valid TopicApplication app) {
    appDao.createOrUpdate(app);
    if (app.getId() == null)
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);

    // TODO: notify topic creator and supervisor(s)

    return Response.created(UriBuilder.fromResource(TopicApplicationResource.class).path("/{id}").build(app.getId()))
        .entity(app).build();
  }
}
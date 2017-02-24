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
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.Status;

import org.pac4j.jax.rs.annotations.Pac4JSecurity;

import cz.studenthub.core.TopicApplication;
import cz.studenthub.db.TopicApplicationDAO;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;

@Path("/applications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Pac4JSecurity(authorizers = "isAdmin", clients = { "DirectBasicAuthClient", "jwtClient" })
public class TopicApplicationResource {

  private final TopicApplicationDAO appDao;

  public TopicApplicationResource(TopicApplicationDAO appDao) {
    this.appDao = appDao;
  }

  @GET
  @UnitOfWork
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
  public Response delete(@PathParam("id") LongParam id) {
    appDao.delete(appDao.findById(id.get()));
    return Response.noContent().build();
  }

  @PUT
  @Path("/{id}")
  @UnitOfWork
  public Response update(@PathParam("id") LongParam id, @NotNull @Valid TopicApplication t) {
    t.setId(id.get());
    appDao.createOrUpdate(t);
    return Response.ok(t).build();
  }

  @POST
  @UnitOfWork
  public Response create(@NotNull @Valid TopicApplication t) {
    appDao.createOrUpdate(t);
    if (t.getId() == null)
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);

    return Response.created(UriBuilder.fromResource(TopicApplicationResource.class).path("/{id}").build(t.getId()))
        .entity(t).build();
  }
  

  // TODO: new form endpoint(s) to modify (FormClient)
}

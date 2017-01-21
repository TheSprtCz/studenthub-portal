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
import javax.ws.rs.core.Response.Status;

import org.pac4j.jax.rs.annotations.Pac4JSecurity;

import javax.ws.rs.core.UriBuilder;

import cz.studenthub.core.University;
import cz.studenthub.db.UniversityDAO;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;

@Path("/universities")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Pac4JSecurity(authorizers = "isAdmin", clients = "DirectBasicAuthClient")
public class UniversityResource {

  private final UniversityDAO uniDao;

  public UniversityResource(UniversityDAO uniDao) {
    this.uniDao = uniDao;
  }

  @GET
  @UnitOfWork
  @Pac4JSecurity(ignore = true)
  public List<University> fetch() {
    return uniDao.findAll();
  }

  @GET
  @Path("/{id}")
  @UnitOfWork
  @Pac4JSecurity(ignore = true)
  public University findById(@PathParam("id") LongParam id) {
    return uniDao.findById(id.get());
  }

  @DELETE
  @Path("/{id}")
  @UnitOfWork
  public Response delete(@PathParam("id") LongParam id) {
    uniDao.delete(uniDao.findById(id.get()));
    return Response.noContent().build();
  }

  @PUT
  @Path("/{id}")
  @UnitOfWork
  public Response update(@PathParam("id") LongParam id, @NotNull @Valid University u) {
    u.setId(id.get());
    uniDao.createOrUpdate(u);
    return Response.ok(u).build();
  }

  @POST
  @UnitOfWork
  public Response create(@NotNull @Valid University u) {
    uniDao.createOrUpdate(u);
    if (u.getId() == null)
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    
    return Response.created(UriBuilder.fromResource(UniversityResource.class).path("/{id}").build(u.getId())).entity(u).build();
  }
}

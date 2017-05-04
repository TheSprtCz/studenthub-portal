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
import static cz.studenthub.auth.Consts.BASIC_AUTH;
import static cz.studenthub.auth.Consts.JWT_AUTH;

import java.util.List;

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

import org.pac4j.jax.rs.annotations.Pac4JSecurity;

import cz.studenthub.core.Faculty;
import cz.studenthub.core.University;
import cz.studenthub.db.FacultyDAO;
import cz.studenthub.db.UniversityDAO;
import cz.studenthub.util.PagingUtil;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.IntParam;
import io.dropwizard.jersey.params.LongParam;

@Path("/universities")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Pac4JSecurity(authorizers = ADMIN, clients = { BASIC_AUTH, JWT_AUTH })
public class UniversityResource {

  private final UniversityDAO uniDao;
  private final FacultyDAO facDao;

  public UniversityResource(UniversityDAO uniDao, FacultyDAO facDao) {
    this.uniDao = uniDao;
    this.facDao = facDao;
  }

  @GET
  @UnitOfWork
  @Pac4JSecurity(ignore = true)
  public List<University> fetch() {
    return uniDao.findAll();
  }

  @GET
  @Path("/search")
  @UnitOfWork
  @Pac4JSecurity(ignore = true)
  public List<University> search(@NotNull @QueryParam("text") String text,
          @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam) {

    return PagingUtil.paging(uniDao.search(text), startParam.get(), sizeParam.get());
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
  public Response delete(@PathParam("id") LongParam idParam) {
    Long id = idParam.get();
    University university = uniDao.findById(id);
    if (university == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    uniDao.delete(university);
    return Response.noContent().build();
  }

  @PUT
  @Path("/{id}")
  @UnitOfWork
  public Response update(@PathParam("id") LongParam idParam, @NotNull @Valid University university) {
    Long id = idParam.get();
    if (uniDao.findById(id) == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    university.setId(id);
    uniDao.update(university);
    return Response.ok(university).build();
  }

  @POST
  @UnitOfWork
  public Response create(@NotNull @Valid University university) {
    uniDao.create(university);
    if (university.getId() == null)
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);

    return Response.created(UriBuilder.fromResource(UniversityResource.class).path("/{id}").build(university.getId()))
        .entity(university).build();
  }

  @GET
  @Path("/{id}/faculties")
  @UnitOfWork
  @Pac4JSecurity(ignore = true)
  public List<Faculty> fetchFaculties(@PathParam("id") LongParam id) {
    University university = uniDao.findById(id.get());
    if (university == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    return facDao.findAllByUniversity(university);
  }

  @GET
  @Path("/{id}/faculties/search")
  @UnitOfWork
  @Pac4JSecurity(ignore = true)
  public List<Faculty> searchFaculties(@PathParam("id") LongParam id, @NotNull @QueryParam("text") String text,
          @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
	      @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam) {

    University university = uniDao.findById(id.get());
    if (university == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    return PagingUtil.paging(facDao.search(university, text), startParam.get(), sizeParam.get());
  }
}

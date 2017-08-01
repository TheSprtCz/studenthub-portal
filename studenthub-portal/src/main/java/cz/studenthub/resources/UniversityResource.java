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

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
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

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;

import cz.studenthub.core.Faculty;
import cz.studenthub.core.University;
import cz.studenthub.core.User;
import cz.studenthub.core.UserRole;
import cz.studenthub.db.FacultyDAO;
import cz.studenthub.db.UniversityDAO;
import cz.studenthub.db.UserDAO;
import cz.studenthub.util.PagingUtil;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.IntParam;
import io.dropwizard.jersey.params.LongParam;

@Path("/universities")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UniversityResource {

  @Inject
  private UniversityDAO uniDao;

  @Inject
  private FacultyDAO facDao;

  @Inject
  private UserDAO userDao;

  @GET
  @UnitOfWork
  @Timed
  public List<University> fetch(@Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam) {
    return PagingUtil.paging(uniDao.findAll(), startParam.get(), sizeParam.get());
  }

  @GET
  @Path("/search")
  @UnitOfWork
  public List<University> search(@NotNull @QueryParam("text") String text,
          @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam) {

    return PagingUtil.paging(uniDao.search(text), startParam.get(), sizeParam.get());
  }

  @GET
  @Path("/{id}")
  @UnitOfWork
  public University findById(@PathParam("id") LongParam id) {
    return uniDao.findById(id.get());
  }

  @DELETE
  @ExceptionMetered
  @Path("/{id}")
  @UnitOfWork
  @RolesAllowed("ADMIN")
  public Response delete(@PathParam("id") LongParam idParam) {
    Long id = idParam.get();
    University university = uniDao.findById(id);
    if (university == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    uniDao.delete(university);
    return Response.noContent().build();
  }

  @PUT
  @ExceptionMetered
  @Path("/{id}")
  @UnitOfWork
  @RolesAllowed({ "ADMIN", "UNIVERSITY_AMB" })
  public Response update(@PathParam("id") LongParam idParam, @NotNull @Valid University university, @Auth User user) {
    Long id = idParam.get();
    University oldUni = uniDao.findById(id);
    if (oldUni == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    if (id.equals(user.getFaculty().getUniversity().getId()) || user.isAdmin()) {
      university.setId(id);
      uniDao.update(university);
      return Response.ok(university).build();
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @POST
  @ExceptionMetered
  @UnitOfWork
  @RolesAllowed("ADMIN")
  public Response create(@NotNull @Valid University university) {
    uniDao.create(university);
    if (university.getId() == null)
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);

    return Response.created(UriBuilder.fromResource(UniversityResource.class).path("/{id}").build(university.getId()))
        .entity(university).build();
  }

  @GET
  @Timed
  @Path("/{id}/faculties")
  @UnitOfWork
  public List<Faculty> fetchFaculties(@PathParam("id") LongParam id,
          @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam) {

    University university = uniDao.findById(id.get());
    if (university == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    return PagingUtil.paging(facDao.findAllByUniversity(university), startParam.get(), sizeParam.get());
  }

  @GET
  @Timed
  @Path("/{id}/supervisors")
  @UnitOfWork
  @RolesAllowed({ "ADMIN", "UNIVERSITY_AMB" })
  public List<User> fetchSupervisors(@Auth User user, @PathParam("id") LongParam idParam,
          @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam) {

    Long id = idParam.get();
    University university = uniDao.findById(id);
    if (university == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    if (user.getFaculty().getUniversity().getId().equals(id) || user.isAdmin()) {
      return PagingUtil.paging(userDao.findByRoleAndUniversity(UserRole.AC_SUPERVISOR, university), startParam.get(), sizeParam.get());
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @GET
  @Path("/{id}/faculties/search")
  @UnitOfWork
  public List<Faculty> searchFaculties(@PathParam("id") LongParam id, @NotNull @QueryParam("text") String text,
        @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
	      @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam) {

    University university = uniDao.findById(id.get());
    if (university == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    return PagingUtil.paging(facDao.search(university, text), startParam.get(), sizeParam.get());
  }
}

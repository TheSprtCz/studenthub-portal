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

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.IntParam;
import io.dropwizard.jersey.params.LongParam;
import net.thesishub.core.Faculty;
import net.thesishub.core.Project;
import net.thesishub.core.User;
import net.thesishub.core.UserRole;
import net.thesishub.db.FacultyDAO;
import net.thesishub.db.ProjectDAO;
import net.thesishub.db.UserDAO;
import net.thesishub.util.PagingUtil;

@Path("/faculties")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FacultyResource {

  @Inject
  private FacultyDAO facDao;

  @Inject
  private UserDAO userDao;

  @Inject
  private ProjectDAO projectDao;

  @GET
  @UnitOfWork
  @Timed
  public List<Faculty> fetch(@Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
          @Context HttpServletResponse response) {
      return PagingUtil.paging(facDao.findAll(), startParam.get(), sizeParam.get(), response);
  }

  @GET
  @Path("/{id}")
  @UnitOfWork
  public Faculty findById(@PathParam("id") LongParam id) {
    return facDao.findById(id.get());
  }

  @DELETE
  @ExceptionMetered
  @Path("/{id}")
  @UnitOfWork
  @RolesAllowed({ "ADMIN", "UNIVERSITY_AMB" })
  public Response delete(@PathParam("id") LongParam idParam, @Auth User user) {
    Long id = idParam.get();
    Faculty faculty = facDao.findById(id);
    if (faculty == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    // User can delete only faculties belonging to his university
    if (faculty.getUniversity().getId().equals(user.getFaculty().getUniversity().getId()) || user.isAdmin()) {
      facDao.delete(faculty);
      return Response.noContent().build();
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @PUT
  @ExceptionMetered
  @Path("/{id}")
  @UnitOfWork
  @RolesAllowed({ "ADMIN", "UNIVERSITY_AMB" })
  public Response update(@PathParam("id") LongParam idParam, @NotNull @Valid Faculty faculty, @Auth User user) {
    Long id = idParam.get();
    Faculty oldFaculty = facDao.findById(id);
    if (oldFaculty == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    Long uniId = user.getFaculty().getUniversity().getId();
    // User must not change university and have the same university as the old one or be Admin
    if ((faculty.getUniversity().getId().equals(uniId) && oldFaculty.getUniversity().getId().equals(uniId))
        || user.isAdmin()) {

      faculty.setId(id);
      facDao.update(faculty);
      return Response.ok(faculty).build();
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @POST
  @ExceptionMetered
  @UnitOfWork
  @RolesAllowed({ "ADMIN", "UNIVERSITY_AMB" })
  public Response create(@NotNull @Valid Faculty faculty, @Auth User user) {
    // User can create only faculties for his university
    if (faculty.getUniversity().getId().equals(user.getFaculty().getUniversity().getId()) || user.isAdmin()) {
      facDao.create(faculty);
      if (faculty.getId() == null)
        throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
  
      return Response.created(UriBuilder.fromResource(FacultyResource.class).path("/{id}").build(faculty.getId()))
          .entity(faculty).build();
    }
    else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @GET
  @Timed
  @Path("/{id}/students")
  @UnitOfWork
  @PermitAll
  public List<User> fetchStudents(@PathParam("id") LongParam id,
          @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
          @Context HttpServletResponse response) {

    Faculty faculty = facDao.findById(id.get());
    if (faculty == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    return PagingUtil.paging(userDao.findByRoleAndFaculty(UserRole.STUDENT, faculty), startParam.get(), sizeParam.get(), response);
  }

  @GET
  @Path("/{id}/supervisors")
  @UnitOfWork
  @PermitAll
  public List<User> fetchSupervisors(@PathParam("id") LongParam id,
          @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
          @Context HttpServletResponse response) {

    Faculty faculty = facDao.findById(id.get());
    if (faculty == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    return PagingUtil.paging(userDao.findByRoleAndFaculty(UserRole.AC_SUPERVISOR, faculty), startParam.get(), sizeParam.get(), response);
  }

  @GET
  @Path("/{id}/projects")
  @UnitOfWork
  @PermitAll
  public List<Project> fetchProjects(@PathParam("id") LongParam id,
          @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
          @Context HttpServletResponse response) {

    Faculty faculty = facDao.findById(id.get());
    if (faculty == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    return PagingUtil.paging(projectDao.findByFaculty(faculty), startParam.get(), sizeParam.get(), response);
  }
}

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

import cz.studenthub.core.Company;
import cz.studenthub.core.CompanyPlan;
import cz.studenthub.core.Project;
import cz.studenthub.core.Topic;
import cz.studenthub.core.User;
import cz.studenthub.core.UserRole;
import cz.studenthub.db.CompanyDAO;
import cz.studenthub.db.ProjectDAO;
import cz.studenthub.db.TopicDAO;
import cz.studenthub.db.UserDAO;
import cz.studenthub.util.PagingUtil;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.IntParam;
import io.dropwizard.jersey.params.LongParam;

@Path("/companies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CompanyResource {


  @Inject
  private CompanyDAO companyDao;

  @Inject
  private UserDAO userDao;

  @Inject
  private TopicDAO topicDao;

  @Inject
  private ProjectDAO projectDao;

  @GET
  @Timed
  @UnitOfWork
  public List<Company> fetch(@Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
          @Context HttpServletResponse response) {
    return PagingUtil.paging(companyDao.findAll(), startParam.get(), sizeParam.get(), response);
  }

  @GET
  @Path("/{id}")
  @UnitOfWork
  public Company findById(@PathParam("id") LongParam id) {
    return companyDao.findById(id.get());
  }

  @POST
  @ExceptionMetered
  @UnitOfWork
  @RolesAllowed("ADMIN")
  public Response create(@NotNull @Valid Company company) {
    Company returned = companyDao.create(company);
    if (returned.getId() == null)
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);

    return Response.created(UriBuilder.fromResource(CompanyResource.class).path("/{id}").build(company.getId()))
        .entity(company).build();
  }

  @PUT
  @ExceptionMetered
  @Path("/{id}")
  @UnitOfWork
  @RolesAllowed("ADMIN")
  public Response update(@PathParam("id") LongParam idParam, @NotNull @Valid Company company) {
    Long id = idParam.get();
    if (companyDao.findById(id) == null) 
      throw new WebApplicationException(Status.NOT_FOUND);

    company.setId(id);
    companyDao.update(company);
    return Response.ok(company).build();
  }

  @DELETE
  @ExceptionMetered
  @Path("/{id}")
  @UnitOfWork
  @RolesAllowed("ADMIN")
  public Response delete(@PathParam("id") LongParam idParam) {
    Long id = idParam.get();
    Company company = companyDao.findById(id);
    if (company == null) 
      throw new WebApplicationException(Status.NOT_FOUND);

    companyDao.delete(company);
    return Response.noContent().build();
  }

  @GET
  @Timed
  @Path("/{id}/leaders")
  @UnitOfWork
  @PermitAll
  public List<User> fetchLeaders(@PathParam("id") LongParam id,
		  @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
		  @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
      @Context HttpServletResponse response) {

    Company company = companyDao.findById(id.get());
    if (company == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    return PagingUtil.paging(userDao.findByRoleAndCompany(UserRole.TECH_LEADER, company), startParam.get(), sizeParam.get(), response);
  }

  @GET
  @Timed
  @Path("/{id}/topics")
  @UnitOfWork
  public List<Topic> fetchTopics(@PathParam("id") LongParam id,
          @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
          @Context HttpServletResponse response) {

    Company company = companyDao.findById(id.get());
    if (company == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    return PagingUtil.paging(topicDao.findByCompany(company), startParam.get(), sizeParam.get(), response);
  }

  @GET
  @Path("/{id}/plan")
  @UnitOfWork
  @RolesAllowed({"COMPANY_REP", "ADMIN"})
  public CompanyPlan fetchPlan(@PathParam("id") LongParam id, @Auth User user) {
    Company company = companyDao.findById(id.get());
    if (company == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    Company userCompany = user.getCompany();
    // If user's company is the same as the company he want to view or he is admin
    if ((userCompany != null && userCompany.getId().equals(company.getId())) || user.getRoles().contains(UserRole.ADMIN)) {
      return company.getPlan();
    }
    else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @GET
  @Timed
  @Path("/{id}/projects")
  @UnitOfWork
  public List<Project> fetchProjects(@PathParam("id") LongParam id,
          @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
          @Context HttpServletResponse response) {

    Company company = companyDao.findById(id.get());
    if (company == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    return PagingUtil.paging(projectDao.findByCompany(company), startParam.get(), sizeParam.get(), response);
  }
}

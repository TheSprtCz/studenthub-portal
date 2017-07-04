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

import cz.studenthub.core.CompanyPlan;
import cz.studenthub.db.CompanyPlanDAO;
import cz.studenthub.util.PagingUtil;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.IntParam;

@Path("/plans")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CompanyPlanResource {

  @Inject
  private CompanyPlanDAO cpDao;

  @GET
  @Timed
  @UnitOfWork
  public List<CompanyPlan> fetch(@Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam) {
    return PagingUtil.paging(cpDao.findAll(), startParam.get(), sizeParam.get());
  }

  @GET
  @Path("/{name}")
  @UnitOfWork
  public CompanyPlan findByName(@PathParam("name") String name) {
    return cpDao.findByName(name);
  }

  @POST
  @ExceptionMetered
  @UnitOfWork
  @RolesAllowed("ADMIN")
  public Response create(@NotNull @Valid CompanyPlan companyPlan) {
    CompanyPlan returned = cpDao.create(companyPlan);

    return Response.created(UriBuilder.fromResource(CompanyPlanResource.class).path("/{name}").build(returned.getName()))
        .entity(companyPlan).build();
  }

  @PUT
  @ExceptionMetered
  @Path("/{name}")
  @UnitOfWork
  @RolesAllowed("ADMIN")
  public Response update(@PathParam("name") String name, @NotNull @Valid CompanyPlan companyPlan) {
    if (cpDao.findByName(name) == null) 
      throw new WebApplicationException(Status.NOT_FOUND);

    companyPlan.setName(name);
    cpDao.update(companyPlan);
    return Response.ok(companyPlan).build();
  }

  @DELETE
  @ExceptionMetered
  @Path("/{name}")
  @UnitOfWork
  @RolesAllowed("ADMIN")
  public Response delete(@PathParam("name") String name) {
    CompanyPlan companyPlan = cpDao.findByName(name);
    if (companyPlan == null) 
      throw new WebApplicationException(Status.NOT_FOUND);

    cpDao.delete(companyPlan);
    return Response.noContent().build();
  }

}

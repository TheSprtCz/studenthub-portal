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

import cz.studenthub.core.Country;
import cz.studenthub.db.CountryDAO;
import cz.studenthub.util.PagingUtil;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.IntParam;

@Path("/countries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CountryResource {

  @Inject
  private CountryDAO countryDao;

  @GET
  @Timed
  @UnitOfWork
  public List<Country> fetch(@Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
          @Context HttpServletResponse response) {
    return PagingUtil.paging(countryDao.findAll(), startParam.get(), sizeParam.get(), response);
  }

  @GET
  @Path("/{tag}")
  @UnitOfWork
  public Country findByName(@PathParam("tag") String name) {
    return countryDao.findByTag(name);
  }

  @POST
  @ExceptionMetered
  @UnitOfWork
  @RolesAllowed("ADMIN")
  public Response create(@NotNull @Valid Country country) {
    countryDao.create(country);

    return Response.created(UriBuilder.fromResource(CountryResource.class).path("/{tag}").build(country.getTag()))
        .entity(country).build();
  }

  @PUT
  @ExceptionMetered
  @Path("/{tag}")
  @UnitOfWork
  @RolesAllowed("ADMIN")
  public Response update(@PathParam("tag") String tag, @NotNull @Valid Country country) {
    if (countryDao.findByTag(tag) == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    country.setTag(tag);
    countryDao.update(country);
    return Response.ok(country).build();
  }

  @DELETE
  @ExceptionMetered
  @Path("/{tag}")
  @UnitOfWork
  @RolesAllowed("ADMIN")
  public Response delete(@PathParam("tag") String tag) {
    Country country = countryDao.findByTag(tag);
    if (country == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    countryDao.delete(country);
    return Response.noContent().build();
  }

}

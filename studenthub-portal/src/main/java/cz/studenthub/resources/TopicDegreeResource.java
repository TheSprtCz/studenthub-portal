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

import cz.studenthub.core.TopicDegree;
import cz.studenthub.db.TopicDegreeDAO;
import cz.studenthub.util.PagingUtil;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.IntParam;

@Path("/degrees")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TopicDegreeResource {

  @Inject
  private TopicDegreeDAO tdDao;

  @GET
  @Timed
  @UnitOfWork
  public List<TopicDegree> fetch(@Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
          @Context HttpServletResponse response) {
    return PagingUtil.paging(tdDao.findAll(), startParam.get(), sizeParam.get(), response);
  }

  @GET
  @Path("/{name}")
  @UnitOfWork
  public TopicDegree findByName(@PathParam("name") String name) {
    return tdDao.findByName(name);
  }

  @POST
  @ExceptionMetered
  @UnitOfWork
  @RolesAllowed("ADMIN")
  public Response create(@NotNull @Valid TopicDegree topicDegree) {
    tdDao.create(topicDegree);

    return Response.created(UriBuilder.fromResource(TopicDegreeResource.class).path("/{name}").build(topicDegree.getName()))
        .entity(topicDegree).build();
  }

  @PUT
  @ExceptionMetered
  @Path("/{name}")
  @UnitOfWork
  @RolesAllowed("ADMIN")
  public Response update(@PathParam("name") String name, @NotNull @Valid TopicDegree topicDegree) {
    if (tdDao.findByName(name) == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    topicDegree.setName(name);
    tdDao.update(topicDegree);
    return Response.ok(topicDegree).build();
  }

  @DELETE
  @ExceptionMetered
  @Path("/{name}")
  @UnitOfWork
  @RolesAllowed("ADMIN")
  public Response delete(@PathParam("name") String name) {
    TopicDegree topicDegree = tdDao.findByName(name);
    if (topicDegree == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    tdDao.delete(topicDegree);
    return Response.noContent().build();
  }

}

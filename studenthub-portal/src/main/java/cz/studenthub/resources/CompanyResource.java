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

import org.pac4j.jax.rs.annotations.Pac4JSecurity;

import cz.studenthub.core.Company;
import cz.studenthub.core.Topic;
import cz.studenthub.core.User;
import cz.studenthub.core.UserRole;
import cz.studenthub.db.CompanyDAO;
import cz.studenthub.db.TopicDAO;
import cz.studenthub.db.UserDAO;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;

@Path("/companies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Pac4JSecurity(authorizers = ADMIN, clients = { BASIC_AUTH, JWT_AUTH })
public class CompanyResource {

  private final CompanyDAO companyDao;
  private final UserDAO userDao;
  private final TopicDAO topicDao;

  public CompanyResource(CompanyDAO companyDao, UserDAO userDao, TopicDAO topicDao) {
    this.companyDao = companyDao;
    this.userDao = userDao;
    this.topicDao = topicDao;
  }

  @GET
  @UnitOfWork
  @Pac4JSecurity(ignore = true)
  public List<Company> fetch() {
    return companyDao.findAll();
  }

  @GET
  @Path("/{id}")
  @UnitOfWork
  @Pac4JSecurity(ignore = true)
  public Company findById(@PathParam("id") LongParam id) {
    return companyDao.findById(id.get());
  }

  @POST
  @UnitOfWork
  public Response create(@NotNull @Valid Company company) {
    Company returned = companyDao.createOrUpdate(company);
    if (returned.getId() == null)
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);

    return Response.created(UriBuilder.fromResource(CompanyResource.class).path("/{id}").build(company.getId()))
        .entity(company).build();
  }

  @PUT
  @Path("/{id}")
  @UnitOfWork
  public Response update(@PathParam("id") LongParam id, @NotNull @Valid Company company) {
    company.setId(id.get());
    companyDao.createOrUpdate(company);
    return Response.ok(company).build();
  }

  @DELETE
  @Path("/{id}")
  @UnitOfWork
  public Response delete(@PathParam("id") LongParam id) {
    companyDao.delete(companyDao.findById(id.get()));
    return Response.noContent().build();
  }

  @GET
  @Path("/{id}/leaders")
  @UnitOfWork
  @Pac4JSecurity(authorizers = AUTHENTICATED, clients = { BASIC_AUTH, JWT_AUTH })
  public List<User> fetchLeaders(@PathParam("id") LongParam id) {
    Company company = companyDao.findById(id.get());
    if (company == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    return userDao.findByRoleAndCompany(UserRole.TECH_LEADER, company);
  }

  @GET
  @Path("/{id}/topics")
  @UnitOfWork
  @Pac4JSecurity(ignore = true)
  public List<Topic> fetchTopics(@PathParam("id") LongParam id) {
    Company company = companyDao.findById(id.get());
    if (company == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    return topicDao.findByCompany(company);
  }

}

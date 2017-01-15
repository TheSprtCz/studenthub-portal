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
import javax.ws.rs.core.UriBuilder;

import cz.studenthub.core.Company;
import cz.studenthub.db.CompanyDAO;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;

@Path("/companies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CompanyResource {

  private final CompanyDAO companyDao;

  public CompanyResource(CompanyDAO companyDao) {
    this.companyDao = companyDao;
  }

  @GET
  @UnitOfWork
  public List<Company> fetch() {
    return companyDao.findAll();
  }

  @GET
  @Path("/{id}")
  @UnitOfWork
  public Company findById(@PathParam("id") LongParam id) {
    return companyDao.findById(id.get());
  }

  @POST
  @UnitOfWork
  public Response create(@NotNull @Valid Company c) {
    companyDao.createOrUpdate(c);
    if (c.getId() == null)
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);

    return Response.created(UriBuilder.fromResource(CompanyResource.class).build(c.getId())).entity(c).build();
  }

  @PUT
  @Path("/{id}")
  @UnitOfWork
  public Response update(@PathParam("id") LongParam id, @NotNull @Valid Company company) {
    if (companyDao.findById(id.get()) == null)
      throw new WebApplicationException(Status.BAD_REQUEST);

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
}

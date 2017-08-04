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

import cz.studenthub.core.Company;
import cz.studenthub.core.Faculty;
import cz.studenthub.core.Project;
import cz.studenthub.core.Topic;
import cz.studenthub.core.TopicApplication;
import cz.studenthub.core.User;
import cz.studenthub.core.UserRole;
import cz.studenthub.db.ProjectDAO;
import cz.studenthub.db.TopicApplicationDAO;
import cz.studenthub.db.TopicDAO;
import cz.studenthub.util.PagingUtil;
import cz.studenthub.validators.groups.CreateUpdateChecks;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.IntParam;
import io.dropwizard.jersey.params.LongParam;
import io.dropwizard.validation.Validated;

@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectResource {

  @Inject
  private ProjectDAO projectDao;

  @Inject
  private TopicApplicationDAO appDao;

  @Inject
  private TopicDAO topicDao;

  @GET
  @Timed
  @UnitOfWork
  public List<Project> fetch(@Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
          @Context HttpServletResponse response) {
    return PagingUtil.paging(projectDao.findAll(), startParam.get(), sizeParam.get(), response);
  }

  @GET
  @Path("/{id}")
  @UnitOfWork
  public Project findById(@PathParam("id") LongParam id) {
    return projectDao.findById(id.get());
  }

  @POST
  @ExceptionMetered
  @UnitOfWork
  @RolesAllowed({"ADMIN", "PROJECT_LEADER"})
  public Response create(@NotNull @Valid @Validated(CreateUpdateChecks.class) Project project, @Auth User user) {

    // If user is creator or admin
    if (project.getCreators().contains(user) || user.isAdmin()) {
      Project returned = projectDao.create(project);
      if (returned.getId() == null)
        throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
  
      return Response.created(UriBuilder.fromResource(ProjectResource.class).path("/{id}").build(project.getId()))
          .entity(project).build();
    }
    else {
      throw new WebApplicationException("You cannot create project where you are not creator.", Status.BAD_REQUEST);
    }
  }

  @PUT
  @ExceptionMetered
  @Path("/{id}")
  @UnitOfWork
  @RolesAllowed({"ADMIN", "PROJECT_LEADER"})
  public Response update(@PathParam("id") LongParam idParam, @NotNull @Valid @Validated(CreateUpdateChecks.class) Project project, @Auth User user) {
    Long id = idParam.get();
    Project oldProject = projectDao.findById(id);
    if (oldProject == null) 
      throw new WebApplicationException(Status.NOT_FOUND);

    // If user is creator or admin
    if (oldProject.getCreators().contains(user) || user.isAdmin()) {
      project.setId(id);
      Project updated = projectDao.update(project);
      return Response.ok(updated).build();
    }
    else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @DELETE
  @ExceptionMetered
  @Path("/{id}")
  @UnitOfWork
  @RolesAllowed({"ADMIN", "PROJECT_LEADER"})
  public Response delete(@PathParam("id") LongParam idParam, @Auth User user) {
    Long id = idParam.get();
    Project project = projectDao.findById(id);
    if (project == null) 
      throw new WebApplicationException(Status.NOT_FOUND);

    if (project.getCreators().contains(user) || user.isAdmin()) {
      projectDao.delete(project);
      return Response.noContent().build();
    }
    else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @GET
  @Timed
  @Path("/{id}/topics")
  @UnitOfWork
  public List<Topic> fetchTopics(@PathParam("id") LongParam id,
		  @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
		  @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
      @Context HttpServletResponse response) {

    Project project = projectDao.findById(id.get());
    if (project == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    // Remove all topics that are not enabled
    project.getTopics().removeIf(topic -> !topic.isEnabled());

    return PagingUtil.paging(project.getTopics(), startParam.get(), sizeParam.get(), response);
  }

  @GET
  @Timed
  @Path("/{id}/applications")
  @UnitOfWork
  public List<TopicApplication> fetchApplications(@PathParam("id") LongParam id,
      @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
      @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
      @Context HttpServletResponse response) {

    Project project = projectDao.findById(id.get());
    if (project == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    return PagingUtil.paging(appDao.findByTopics(project.getTopics()), startParam.get(), sizeParam.get(), response);
  }

  @GET
  @Timed
  @Path("/{id}/companies")
  @UnitOfWork
  public List<Company> fetchCompanies(@PathParam("id") LongParam id,
          @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
          @Context HttpServletResponse response) {

    Project project = projectDao.findById(id.get());
    if (project == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    return PagingUtil.paging(project.getCompanies(), startParam.get(), sizeParam.get(), response);
  }

  @GET
  @Timed
  @Path("/{id}/faculties")
  @UnitOfWork
  public List<Faculty> fetchFaculties(@PathParam("id") LongParam id,
          @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
          @Context HttpServletResponse response) {

    Project project = projectDao.findById(id.get());
    if (project == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    return PagingUtil.paging(project.getFaculties(), startParam.get(), sizeParam.get(), response);
  }

  @GET
  @Timed
  @Path("/{id}/creators")
  @UnitOfWork
  public List<User> fetchCreators(@PathParam("id") LongParam id,
          @Min(0) @DefaultValue("0") @QueryParam("start") IntParam startParam,
          @Min(0) @DefaultValue("0") @QueryParam("size") IntParam sizeParam,
          @Context HttpServletResponse response) {

    Project project = projectDao.findById(id.get());
    if (project == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    return PagingUtil.paging(project.getCreators(), startParam.get(), sizeParam.get(), response);
  }

  @PUT
  @Timed
  @Path("/{id}/assignTopic/{topicId}")
  @UnitOfWork
  @RolesAllowed({"ADMIN", "PROJECT_LEADER", "COMPANY_REP"})
  public Response assignTopic(@PathParam("id") LongParam idParam, @PathParam("topicId") LongParam topicIdParam, @Auth User user) {

    Long id = idParam.get();
    Project project = projectDao.findById(id);
    Topic topic = topicDao.findById(topicIdParam.get());
    if (project == null || topic == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    // If user is creator or he is company rep of associated company and topic or admin
    if (project.getCreators().contains(user) || (user.hasRole(UserRole.COMPANY_REP) && project.getCompanies().contains(user.getCompany())
        && topic.getCreator().getCompany().equals(user.getCompany())) || user.isAdmin()) {

      if (project.getTopics().contains(topic))
        throw new WebApplicationException("Topic is already assigned to this project", Status.CONFLICT);

      project.getTopics().add(topic);
      projectDao.update(project);
      return Response.ok().build();
    }
    else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }
}

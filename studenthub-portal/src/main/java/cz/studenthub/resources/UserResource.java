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
import static cz.studenthub.auth.Consts.STUDENT;
import static cz.studenthub.auth.Consts.SUPERVISOR;
import static cz.studenthub.auth.Consts.TECH_LEADER;

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

import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;

import cz.studenthub.auth.StudentHubPasswordEncoder;
import cz.studenthub.auth.StudentHubProfile;
import cz.studenthub.core.Topic;
import cz.studenthub.core.TopicApplication;
import cz.studenthub.core.User;
import cz.studenthub.core.UserRole;
import cz.studenthub.db.TopicApplicationDAO;
import cz.studenthub.db.TopicDAO;
import cz.studenthub.db.UserDAO;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Pac4JSecurity(authorizers = AUTHENTICATED, clients = { BASIC_AUTH, JWT_AUTH })
public class UserResource {

  private final UserDAO userDao;
  private final TopicApplicationDAO appDao;
  private final TopicDAO topicDao;

  public UserResource(UserDAO userDao, TopicDAO topicDao, TopicApplicationDAO taDao) {
    this.userDao = userDao;
    this.appDao = taDao;
    this.topicDao = topicDao;
  }

  @GET
  @UnitOfWork
  @Pac4JSecurity(authorizers = ADMIN, clients = { BASIC_AUTH, JWT_AUTH })
  public List<User> fetch() {
    return userDao.findAll();
  }

  @GET
  @Path("/{id}")
  @UnitOfWork
  public User findById(@Pac4JProfile StudentHubProfile profile, @PathParam("id") LongParam id) {
    // only admin or profile owner is allowed
    if (id.get().equals(Long.valueOf(profile.getId())) || profile.getRoles().contains(UserRole.ADMIN.name())) {
      return userDao.findById(id.get());
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @DELETE
  @Path("/{id}")
  @UnitOfWork
  public Response delete(@Pac4JProfile StudentHubProfile profile, @PathParam("id") LongParam idParam) {
 
    Long id = idParam.get();
    User user = userDao.findById(id);
    if (user == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    // only admin or profile owner is allowed
    if (id.equals(Long.valueOf(profile.getId())) || profile.getRoles().contains(UserRole.ADMIN.name())) {
      userDao.delete(user);
      return Response.noContent().build();
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }

  }

  @PUT
  @Path("/{id}")
  @UnitOfWork
  @Pac4JSecurity(authorizers = "isAuthenticated", clients = { "DirectBasicAuthClient", "jwtClient" })
  public Response update(@Pac4JProfile StudentHubProfile profile, @PathParam("id") LongParam idParam,
      @NotNull @Valid User user) {
    Long id = idParam.get();
    if (userDao.findById(id) == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    if (id.equals(Long.valueOf(profile.getId())) || profile.getRoles().contains(UserRole.ADMIN.name())) {
      user.setId(id);
      userDao.update(user);
      return Response.ok(user).build();
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  /*
   * Endpoint for public sign up
   */
  @POST
  @Path("/signUp")
  @UnitOfWork
  @Pac4JSecurity(ignore = true)
  public Response signUp(@NotNull @Valid User user) {

    String pwd = new StudentHubPasswordEncoder().encode(user.getPassword());
    user.setPassword(pwd);

    // TODO send conf. email
    userDao.create(user);
    if (user.getId() == null)
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);

    return Response.created(UriBuilder.fromResource(UserResource.class).path("/{id}").build(user.getId())).entity(user)
        .build();
  }

  @GET
  @Path("/{id}/applications")
  @UnitOfWork
  @Pac4JSecurity(authorizers = STUDENT, clients = { BASIC_AUTH, JWT_AUTH })
  public List<TopicApplication> fetchApplications(@Pac4JProfile StudentHubProfile profile,
      @PathParam("id") LongParam id) {

    User user = userDao.findById(id.get());
    if (id.get().equals(Long.valueOf(profile.getId())) || profile.getRoles().contains(UserRole.ADMIN.name())) {
      return appDao.findByStudent(user);
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @GET
  @Path("/{id}/leadApplications")
  @UnitOfWork
  @Pac4JSecurity(authorizers = TECH_LEADER, clients = { BASIC_AUTH, JWT_AUTH })
  public List<TopicApplication> fetchLeadApps(@Pac4JProfile StudentHubProfile profile, @PathParam("id") LongParam id) {

    User user = userDao.findById(id.get());
    if (id.get().equals(Long.valueOf(profile.getId())) || profile.getRoles().contains(UserRole.ADMIN.name())) {
      return appDao.findByLeader(user);
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @GET
  @Path("/{id}/ownedTopics")
  @UnitOfWork
  @Pac4JSecurity(authorizers = TECH_LEADER, clients = { BASIC_AUTH, JWT_AUTH })
  public List<Topic> fetchOwnedTopics(@Pac4JProfile StudentHubProfile profile, @PathParam("id") LongParam id) {
    User user = userDao.findById(id.get());
    if (id.get().equals(Long.valueOf(profile.getId())) || profile.getRoles().contains(UserRole.ADMIN.name())) {
      return topicDao.findByCreator(user);
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @GET
  @Path("/{id}/supervisedTopics")
  @UnitOfWork
  @Pac4JSecurity(authorizers = SUPERVISOR, clients = { BASIC_AUTH, JWT_AUTH })
  public List<Topic> fetchSupervisedTopics(@Pac4JProfile StudentHubProfile profile, @PathParam("id") LongParam id) {
    User user = userDao.findById(id.get());
    if (id.get().equals(Long.valueOf(profile.getId())) || profile.getRoles().contains(UserRole.ADMIN.name())) {
      return topicDao.findBySupervisor(user);
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @GET
  @Path("/{id}/supervisedApplications")
  @UnitOfWork
  @Pac4JSecurity(authorizers = SUPERVISOR, clients = { BASIC_AUTH, JWT_AUTH })
  public List<TopicApplication> fetchSupervisedApplications(@Pac4JProfile StudentHubProfile profile,
      @PathParam("id") LongParam id) {

    User user = userDao.findById(id.get());
    if (id.get().equals(Long.valueOf(profile.getId())) || profile.getRoles().contains(UserRole.ADMIN.name())) {
      return appDao.findBySupervisor(user);
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }
}

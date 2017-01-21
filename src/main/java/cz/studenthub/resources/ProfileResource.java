package cz.studenthub.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pac4j.core.profile.ProfileManager;
import org.pac4j.jax.rs.annotations.Pac4JCallback;
import org.pac4j.jax.rs.annotations.Pac4JLogout;
import org.pac4j.jax.rs.annotations.Pac4JProfileManager;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.studenthub.auth.StudentHubProfile;
import io.dropwizard.hibernate.UnitOfWork;

// TODO:

@Path("/profile")
@Produces(MediaType.TEXT_HTML)
public class ProfileResource {

  Logger LOG = LoggerFactory.getLogger(ProfileResource.class);

  @GET
  @UnitOfWork
  @Path("/session")
  @Produces(MediaType.APPLICATION_JSON)
  @Pac4JSecurity(authorizers = "isAuthenticated")
  public StudentHubProfile getUser(@Pac4JProfileManager ProfileManager<StudentHubProfile> profileM) {
    final StudentHubProfile profile = profileM.get(true).get();

    LOG.info("OBTAINING PROFILE");

    // update last login timestamp
    // User u = userDao.findByEmail(profile.getEmail());
    // u.setLastLogin(new Timestamp(System.currentTimeMillis()));
    // userDao.createOrUpdate(u);

    return profile;
  }

  @POST
  @Path("/callback")
  @Pac4JCallback
  public void login() {
    // action handled by pac4j
  }

  @GET
  @Path("/logout")
  @Pac4JLogout(defaultUrl = "/")
  public void logout() {
    // action handled by pac4j
  }

}

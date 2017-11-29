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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
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

import org.hibernate.validator.constraints.Email;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;
import net.thesishub.api.UpdatePasswordBean;
import net.thesishub.auth.ThesisHubPasswordEncoder;
import net.thesishub.core.Activation;
import net.thesishub.core.ActivationType;
import net.thesishub.core.Faculty;
import net.thesishub.core.User;
import net.thesishub.core.UserRole;
import net.thesishub.db.ActivationDAO;
import net.thesishub.db.FacultyDAO;
import net.thesishub.db.UserDAO;
import net.thesishub.util.Equals;
import net.thesishub.util.MailClient;
import net.thesishub.util.UrlUtil;

/**
 * User registration and password manipulation endpoints
 * 
 * @author sbunciak
 * @since 1.0
 */
@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RegistrationResource {

  @Inject
  private UserDAO userDao;

  @Inject
  private ActivationDAO actDao;

  @Inject
  private FacultyDAO facDao;

  @Inject
  private MailClient mailer;

  @Inject
  private UrlUtil urlUtil;

  @POST
  @Path("/signUp")
  @UnitOfWork
  public Response signUp(@NotNull @Valid User user) {

    // check if email is taken
    User sameEmail = userDao.findByEmail(user.getEmail());
    if (sameEmail != null)
      throw new WebApplicationException("Email is already taken.", Status.CONFLICT);

    // check if username is taken
    User sameUsername = userDao.findByUsername(user.getUsername());
    if (sameUsername != null)
      throw new WebApplicationException("Username is already taken.", Status.CONFLICT);

    // check if someone is not trying to reg as ADMIN
    if (!user.hasOnlyRole(UserRole.STUDENT))
      throw new WebApplicationException("Invalid roles, only STUDENT can register.", Status.BAD_REQUEST);

    // persist user to DB
    userDao.create(user);
    // failed to persist user - server error
    if (user.getId() == null)
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);

    // put user into cache of "inactive" users
    Activation act = new Activation(user, ActivationType.REGISTER);
    actDao.create(act);

    sendActivationEmail(user, act.getActivationCode());

    return Response.created(UriBuilder.fromResource(UserResource.class).path("/{id}").build(user.getId())).entity(user)
        .build();
  }

  @POST
  @Path("/invite")
  @UnitOfWork
  @RolesAllowed({ "ADMIN", "COMPANY_REP", "UNIVERSITY_AMB" })
  public Response invite(@NotNull @Valid User user, @Auth User auth) {

    // check if email is taken
    User sameEmail = userDao.findByEmail(user.getEmail());
    if (sameEmail != null)
      throw new WebApplicationException("Email is already taken.", Status.CONFLICT);

    // check if username is taken
    User sameUsername = userDao.findByUsername(user.getUsername());
    if (sameUsername != null)
      throw new WebApplicationException("Username is already taken.", Status.CONFLICT);

    // Permission checks
    if (!auth.isAdmin()) {
      // Company rep checks, he can only invite COMPANY_REP and TECH_LEADER with same company as he has
      if (auth.hasRole(UserRole.COMPANY_REP)) {
        if (user.hasOnlyOneOfRoles(UserRole.COMPANY_REP, UserRole.TECH_LEADER)) {
          if (!Equals.id(user.getCompany(), auth.getCompany()))
            throw new WebApplicationException("You can only invite users with the same company as you", Status.BAD_REQUEST);
        }
        else {
          throw new WebApplicationException("You cannot invite user with this role", Status.BAD_REQUEST);
        }
      }

      // University ambassador checks, he can only invite UNIVERSITY_AMB and AC_SUPERVISOR with same university as he has
      if (auth.hasRole(UserRole.UNIVERSITY_AMB)) {
        Faculty faculty = facDao.findById(user.getFaculty().getId());
        if (faculty == null)
          throw new WebApplicationException("Specified faculty does not exist", Status.NOT_FOUND);

        if (user.hasOnlyOneOfRoles(UserRole.UNIVERSITY_AMB, UserRole.AC_SUPERVISOR)) {
          if (!Equals.id(faculty.getUniversity(), auth.getFaculty().getUniversity()))
            throw new WebApplicationException("You can only invite users with the same university as you", Status.BAD_REQUEST);
        }
        else {
          throw new WebApplicationException("You cannot invite user with this role", Status.BAD_REQUEST);
        }
      }
    }

    // persist user to DB
    userDao.create(user);
    // failed to persist user - server error
    if (user.getId() == null)
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);

    // put user into cache of "inactive" users
    Activation act = new Activation(user, ActivationType.REGISTER);
    actDao.create(act);

    sendInviteEmail(user, auth.getName(), act.getActivationCode());

    return Response.created(UriBuilder.fromResource(UserResource.class).path("/{id}").build(user.getId())).entity(user)
        .build();
  }

  @POST
  @Path("/activate")
  @UnitOfWork
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response activate(@QueryParam("secret") String secretKey, @QueryParam("id") LongParam idParam,
      @FormParam("password") String password) {
    User user = userDao.findById(idParam.get());
    if (user == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    Activation act = actDao.findByUserAndType(user, ActivationType.REGISTER);
    if (act != null && act.getActivationCode().equals(secretKey)) {
      // hash and update user password
      user.setPassword(ThesisHubPasswordEncoder.encode(password));
      userDao.update(user);
      // send conf. mail
      mailer.sendMessage(user.getEmail(), "Your account has been activated", "activated.html", urlUtil.createLoginUrl());
      // remove user from "inactive" users
      actDao.delete(act);
      return Response.ok().build();
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @POST
  @UnitOfWork
  @Path("/resendActivation")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response resendActivationEmail(@FormParam("email") @Email String email) {
    User user = userDao.findByEmail(email);
    if (user == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    if (user.getPassword() != null)
      throw new WebApplicationException("User is already active.", Status.BAD_REQUEST);

    Activation act = actDao.findByUserAndType(user, ActivationType.REGISTER);
    if (act == null)
      throw new WebApplicationException("No activation to resend.", Status.NOT_FOUND);

    sendActivationEmail(user, act.getActivationCode());

    return Response.ok().build();
  }
  
  @POST
  @UnitOfWork
  @Path("/resetPassword")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response resetPassword(@FormParam("email") String email) {
    User user = userDao.findByEmail(email);
    if (user == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    Activation existing = actDao.findByUser(user);
    // If he has pending password activation
    if (existing != null && existing.getType().equals(ActivationType.REGISTER))
      throw new WebApplicationException("Already pending activation.", Status.BAD_REQUEST);

    Activation activation;
    // If he has pending resetPassword activation, then regenerate code
    if (existing != null && existing.getType().equals(ActivationType.PASSWORD_RESET)) {
      activation = existing;
      activation.setActivationCode(ThesisHubPasswordEncoder.genSecret());
      actDao.update(activation);
    }
    else {
      activation = new Activation(user, ActivationType.PASSWORD_RESET);
      actDao.create(activation);
    }

    sendResetEmail(user, activation.getActivationCode());

    return Response.ok().build();
  }

  @POST
  @UnitOfWork
  @Path("/confirmReset")
  public Response confirmReset(@QueryParam("secret") String secretKey, @QueryParam("id") LongParam idParam) {
    User user = userDao.findById(idParam.get());
    if (user == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    Activation act = actDao.findByUserAndType(user, ActivationType.PASSWORD_RESET);
    if (act != null && act.getActivationCode().equals(secretKey)) {

      // "de-activate" user by un-setting his password
      user.setPassword(null);
      userDao.update(user);

      // put user into cache of "inactive" users
      Activation activation = new Activation(user, ActivationType.REGISTER);
      actDao.create(activation);

      sendActivationEmail(user, activation.getActivationCode());

      return Response.ok().build();
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @PUT
  @Path("/{id}/password")
  @UnitOfWork
  @PermitAll
  public Response updatePassword(@PathParam("id") LongParam idParam, UpdatePasswordBean updateBean, @Auth User auth) {
    // only admin or profile owner is allowed
    Long id = idParam.get();
    User user = userDao.findById(id);
    if (user == null)
      throw new WebApplicationException(Status.NOT_FOUND);

    if (Equals.id(auth, id) || auth.isAdmin()) {
      // check if old password matches
      if (ThesisHubPasswordEncoder.matches(updateBean.getOldPwd(), user.getPassword())) {
        // set new password
        user.setPassword(ThesisHubPasswordEncoder.encode(updateBean.getNewPwd()));
        userDao.update(user);
        mailer.sendMessage(user.getEmail(), "Your password has been changed", "pwdUpdated.html", urlUtil.createLoginUrl());
        return Response.ok().build();
      } else {
        // passwords don't match
        throw new WebApplicationException(Status.BAD_REQUEST);
      }
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  private void sendActivationEmail(User user, String secretKey) {
    // send conf. email with activation link
    Map<String, String> args = new HashMap<String, String>();

    // Build activationURL
    UriBuilder builder = urlUtil.getUriBuilder("activate");
    builder.queryParam("secret", secretKey);
    builder.queryParam("id", user.getId());

    args.put("url", builder.toString());
    args.put("name", user.getName());
    mailer.sendMessage(user.getEmail(), "Activate your account", "setPassword.html", args);
  }

  private void sendResetEmail(User user, String secretKey) {
    // send conf. email with activation link
    Map<String, String> args = new HashMap<String, String>();
    
    // Build activationURL
    UriBuilder builder = urlUtil.getUriBuilder("reset-password");
    builder.queryParam("secret", secretKey);
    builder.queryParam("id", user.getId());
    
    args.put("url", builder.toString());
    args.put("name", user.getName());
    mailer.sendMessage(user.getEmail(), "Your password has been reset", "resetPassword.html", args);
  }

  private void sendInviteEmail(User user, String name, String secretKey) {
    // send conf. email with activation link
    Map<String, String> args = new HashMap<String, String>();

    // Build activationURL
    UriBuilder builder = urlUtil.getUriBuilder("activate");
    builder.queryParam("secret", secretKey);
    builder.queryParam("id", user.getId());

    args.put("url", builder.toString());
    args.put("name", user.getName());
    args.put("by", name);
    mailer.sendMessage(user.getEmail(), "You have been invited", "invited.html", args);
  }
}

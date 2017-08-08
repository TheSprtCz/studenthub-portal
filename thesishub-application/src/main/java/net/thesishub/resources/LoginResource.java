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

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import com.codahale.metrics.annotation.ExceptionMetered;

import io.dropwizard.hibernate.UnitOfWork;
import net.thesishub.auth.ThesisHubPasswordEncoder;
import net.thesishub.core.User;
import net.thesishub.core.UserRole;
import net.thesishub.db.UserDAO;

/**
 * Resource class for obtaining JWT Token
 * 
 * JWT Payload:
 * 
 * <pre>
 * {
    "sub": "1",
    "exp": 1487091708,
    "iat": 1573491708,
    "name": "Student Hub Admin",
    "email": "admin@studenthub.cz",
    "roles": [
      "STUDENT",
      "COMPANY_REP",
      "AC_SUPERVISOR",
      "ADMIN",
      "TECH_LEADER"
    ]
  }
 * </pre>
 * 
 * @author sbunciak
 * @since 1.0
 */
@Path("/auth")
public class LoginResource {

  private static final Logger LOG = LoggerFactory.getLogger(LoginResource.class);

  public static final String BEARER_PREFFIX = "Bearer ";

  public static final String COOKIE_NAME = "sh-token";
  
  private final String jwtSecret;
  
  @Inject
  private UserDAO userDao;

  public LoginResource(String jwtSecret) {
    this.jwtSecret = jwtSecret;
  }

  @POST
  @UnitOfWork
  @ExceptionMetered
  @Path("/login")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response authenticateUser(@FormParam("username") String username, @FormParam("password") String password) {
    User user = userDao.findByEmail(username);
    if (user == null || user.getPassword() == null)
      throw new WebApplicationException(Status.UNAUTHORIZED);
    
    if (ThesisHubPasswordEncoder.matches(password, user.getPassword())) {
      
      Algorithm algorithm = null;
      try {
        algorithm = Algorithm.HMAC256(jwtSecret);
      } catch (IllegalArgumentException | UnsupportedEncodingException e) {
        LOG.error("Error when constructing JWT Algorithm", e);
      }

      // construct JWT token
      Date issuedAt = new Date();
      String token = JWT.create()
          .withSubject(user.getId().toString())
          .withClaim("name", user.getName())
          .withClaim("email", user.getEmail())
          .withArrayClaim("roles", getRolesAsStringArray(user))
          .withIssuer("student-hub")
          .withIssuedAt(issuedAt)
          .withExpiresAt(new Date(issuedAt.getTime() + (1000 * 60 * 60 * 24)))
          .sign(algorithm);

      // update last login timestamp
      user.setLastLogin(new Timestamp(issuedAt.getTime()));
      userDao.update(user);

      // Return the token on the response and store in cookie
      NewCookie newCookie = new NewCookie(COOKIE_NAME, token, "/", "", "", -1, false, false);
      return Response.ok().header(HttpHeaders.AUTHORIZATION, BEARER_PREFFIX + token).cookie(newCookie).build();
    } else {
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }

  @POST
  @Path("/logout")
  @PermitAll
  public Response logout() {
    NewCookie newCookie = new NewCookie(COOKIE_NAME, null, "/", "", 1, "", 0, new Date(0), false, false);
    return Response.ok().cookie(newCookie).build();
  }

  private static String[] getRolesAsStringArray(User user) {
    Set<UserRole> roles = user.getRoles();
    String[] rolesArray = new String[roles.size()];
    int i = 0;
    for (UserRole role : roles) {
      rolesArray[i] = role.toString();
      i++;
    }
    return rolesArray;
  }
}

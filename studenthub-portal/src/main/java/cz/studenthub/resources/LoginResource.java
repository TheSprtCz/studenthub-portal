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

import static cz.studenthub.auth.Consts.AUTHENTICATED;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.profile.JwtGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.studenthub.auth.StudentHubPasswordEncoder;
import cz.studenthub.auth.StudentHubProfile;
import io.dropwizard.hibernate.UnitOfWork;

/**
 * Resource class for obtaining JWT Token
 * 
 * JWT Format: 
 * <pre>
 * {
    "$int_perms": [],
    "sub": "cz.studenthub.auth.StudentHubProfile#1",
    "$int_roles": [
      "STUDENT",
      "COMPANY_REP",
      "AC_SUPERVISOR",
      "ADMIN",
      "TECH_LEADER"
    ],
    "display_name": "Student Hub Admin",
    "iat": 1487091708,
    "email": "admin@studenthub.cz"
  }
  </pre>
 * 
 * @author sbunciak
 * @since 1.0
 */
@Path("/auth")
public class LoginResource {

  private final Logger LOG = LoggerFactory.getLogger(LoginResource.class);

  public static final String BEARER_PREFFIX = "Bearer ";

  @POST
  @UnitOfWork
  @Path("/login")
  @Pac4JSecurity(authorizers = AUTHENTICATED, clients = "DirectFormClient")
  public Response authenticateUser(@Pac4JProfile StudentHubProfile profile) {
    try {
      // Generate random 256-bit (32-byte) shared secret
      String sharedSecret = StudentHubPasswordEncoder.DEFAULT_SECRET;

      // Issue a token for the user
      JwtGenerator<StudentHubProfile> generator = new JwtGenerator<StudentHubProfile>(
          new SecretSignatureConfiguration(sharedSecret));
      String token = generator.generate(profile);

      // TODO: expiration
      // TODO: login timestamp
      // update last login timestamp
      // User u = userDao.findByEmail(profile.getEmail());
      // u.setLastLogin(new Timestamp(System.currentTimeMillis()));
      // userDao.createOrUpdate(u);

      // Return the token on the response
      return Response.ok().header(HttpHeaders.AUTHORIZATION, BEARER_PREFFIX + token).build();
    } catch (Exception e) {
      // otherwise return FORBIDDEN
      LOG.error(e.getMessage(), e);
      throw new WebApplicationException(Status.FORBIDDEN);
    }
  }
}

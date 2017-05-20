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
package cz.studenthub.auth;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import cz.studenthub.core.User;
import cz.studenthub.db.UserDAO;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.hibernate.UnitOfWork;

/**
 * Token based authenticator
 * 
 * @author sbunciak
 * @since 1.0
 */
public class TokenAuthenticator implements Authenticator<String, User> {

  private final JWTVerifier verifier;
  private final UserDAO userDao;
  private final Logger LOG = LoggerFactory.getLogger(TokenAuthenticator.class);

  public TokenAuthenticator(UserDAO userDao) {
    this.userDao = userDao;
    Algorithm algorithm = null;
    try {
      algorithm = Algorithm.HMAC256(StudentHubPasswordEncoder.DEFAULT_SECRET);
    } catch (IllegalArgumentException | UnsupportedEncodingException e) {
      LOG.error("Error constructing JWT Algorithm. Token authentication will most likely not work.", e);
    }
    verifier = JWT.require(algorithm).withIssuer("student-hub").build();
  }

  @Override
  @UnitOfWork
  public Optional<User> authenticate(String token) throws AuthenticationException {
    LOG.debug("Authenticating using token: " + token);
    User user = null;
    try {
      DecodedJWT jwt = verifier.verify(token);
      LOG.debug("Token verified for user id: " + jwt.getSubject());
      user = userDao.findById(Long.parseLong(jwt.getSubject()));
      LOG.debug("Authenticated: " + user);
    } catch (JWTVerificationException e) {
      LOG.debug("Token not authenticated: " + token);
    }

    return Optional.ofNullable(user);
  }
}

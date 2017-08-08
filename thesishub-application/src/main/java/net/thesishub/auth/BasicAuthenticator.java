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
package net.thesishub.auth;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.hibernate.UnitOfWork;
import net.thesishub.core.User;
import net.thesishub.db.UserDAO;

/**
 * Username/password basic auth authenticator
 * 
 * @author sbunciak
 * @since 1.0
 */
public class BasicAuthenticator implements Authenticator<BasicCredentials, User> {

  private final UserDAO userDao;
  private final Logger LOG = LoggerFactory.getLogger(BasicAuthenticator.class);
      
  public BasicAuthenticator(UserDAO userDao) {
    this.userDao = userDao;
  }

  @Override
  @UnitOfWork
  public Optional<User> authenticate(BasicCredentials creds) throws AuthenticationException {
    LOG.debug("Authenticating using Basic Auth user: " + creds.getUsername());
    User user = userDao.findByEmail(creds.getUsername());
    if (user != null && ThesisHubPasswordEncoder.matches(creds.getPassword(), user.getPassword())) {
      LOG.debug("User authenticated: " + user);
      return Optional.of(user);
    } else {
      return Optional.empty();
    }
  }
}

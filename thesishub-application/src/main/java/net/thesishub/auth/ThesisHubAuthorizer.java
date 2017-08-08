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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.auth.Authorizer;
import net.thesishub.core.User;
import net.thesishub.core.UserRole;

/**
 * Dropwizard authorizer to work specifically with UserRole enum.
 * 
 * @author sbunciak
 * @since 1.0
 */
public class ThesisHubAuthorizer implements Authorizer<User> {

  private final Logger LOG = LoggerFactory.getLogger(ThesisHubAuthorizer.class);
  
  @Override
  public boolean authorize(User user, String checkedRole) {
    LOG.debug("Authorizing " + user + "for role " + checkedRole); 
    for (UserRole role : user.getRoles()) {
      if (role.toString().equalsIgnoreCase(checkedRole)) {
        LOG.debug("Role granted: " + checkedRole);
        return true;
      }
    }
    LOG.debug("Role not granted " + checkedRole);
    return false;
  }
}

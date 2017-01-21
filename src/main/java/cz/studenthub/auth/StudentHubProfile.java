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

import java.util.Set;

import org.pac4j.core.profile.CommonProfile;

import cz.studenthub.core.UserRole;

/**
 * Pac4j Profile class for Student Hub.
 * 
 * @author sbunciak
 * @since 1.0
 */
public class StudentHubProfile extends CommonProfile {

  public void setRoles(Set<UserRole> roles) {
    for (UserRole userRole : roles) {
      addRole(userRole.name());
    }
  }

}

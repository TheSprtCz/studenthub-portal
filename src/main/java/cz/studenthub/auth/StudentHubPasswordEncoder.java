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

import org.apache.commons.codec.digest.Crypt;
import org.apache.commons.lang3.RandomStringUtils;
import org.pac4j.core.credentials.password.PasswordEncoder;

/**
 * Helper class for password encoding.
 * 
 * @author sbunciak
 * @since 1.0
 */
public class StudentHubPasswordEncoder implements PasswordEncoder {

  @Override
  public String encode(String password) {
    return Crypt.crypt(password);
  }

  @Override
  public boolean matches(String plainPassword, String encodedPassword) {
    return encodedPassword.equals(Crypt.crypt(plainPassword, encodedPassword));
  }
  
  /*
   * Helper method to generate temp.password
   */
  public static String genPassword() {
    return RandomStringUtils.randomAlphanumeric(32);
  }
}

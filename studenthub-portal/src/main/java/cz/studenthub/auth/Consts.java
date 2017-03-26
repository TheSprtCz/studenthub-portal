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

public final class Consts {

  private Consts() {
    // do not allow init
  }

  /*
   * Roles
   */
  public static final String ADMIN = "isAdmin";
  public static final String STUDENT = "isStudent";
  public static final String TECH_LEADER = "isTechLeader";
  public static final String COMPANY_REP = "isCompanyRep";
  public static final String SUPERVISOR = "isSupervisor";
  public static final String AUTHENTICATED = "isAuthenticated";
  /*
   * Clients
   */
  public static final String BASIC_AUTH = "DirectBasicAuthClient";
  public static final String JWT_AUTH = "jwtClient";
  public static final String[] EVERY_AUTH = new String[] {BASIC_AUTH, JWT_AUTH};
}

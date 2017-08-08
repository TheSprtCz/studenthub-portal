/*******************************************************************************
 *     Copyright (C) 2016  Stefan Bunciak
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
package net.thesishub.health;

import javax.inject.Inject;

import com.codahale.metrics.health.HealthCheck;

import io.dropwizard.hibernate.UnitOfWork;
import net.thesishub.core.UserRole;
import net.thesishub.db.UserDAO;

/**
 * Simple health check to verify DB connection.
 * 
 * @author sbunciak
 * @since 1.0
 */
public class ThesisHubHealthCheck extends HealthCheck {

  @Inject
  private UserDAO userDao;
  
  @UnitOfWork
  @Override
  protected Result check() throws Exception {
    if (!userDao.findByRole(UserRole.ADMIN).isEmpty()) {
      return Result.healthy();  
    } else {
      return Result.unhealthy("No ADMIN user found!");
    }
  }
}

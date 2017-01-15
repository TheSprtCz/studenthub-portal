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
package cz.studenthub.db;

import java.util.List;

import org.hibernate.SessionFactory;

import cz.studenthub.core.User;
import io.dropwizard.hibernate.AbstractDAO;

/**
 * Data(base) Access Object for User objects.
 * 
 * @author sbunciak
 * @since 1.0
 */
public class UserDAO extends AbstractDAO<User> {

  public UserDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public User createOrUpdate(User u) {
    return persist(u);
  }

  public User findById(Long id) {
    return get(id);
  }

  public List<User> findAll() {
    return list(namedQuery("User.findAll"));
  }

  public void delete(User u) {
    currentSession().delete(u);
  }
}

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

import cz.studenthub.core.Company;
import cz.studenthub.core.Faculty;
import cz.studenthub.core.University;
import cz.studenthub.core.User;
import cz.studenthub.core.UserRole;
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
  
  public User update(User user) {
    currentSession().clear();
    return persist(user);
  }

  public User create(User user) {
    return persist(user);
  }

  public User findById(Long id) {
    return get(id);
  }
  
  public User findByEmail(String email) {
    return uniqueResult(namedQuery("User.findByEmail").setParameter("email", email));
  }

  public User findByUsername(String username) {
    return uniqueResult(namedQuery("User.findByUsername").setParameter("username", username));
  }

  public List<User> findByRole(UserRole role) {
    return list(namedQuery("User.findByRole").setParameter("role", role));
  }

  public List<User> findByCompany(Company company) {
    return list(namedQuery("User.findByCompany").setParameter("company", company));
  }

  public List<User> findByTag(String tag) {
    return list(namedQuery("User.findByTag").setParameter("tag", tag));
  }

  public List<User> findByRoleAndFaculty(UserRole role, Faculty faculty) {
    return list(namedQuery("User.findByRoleAndFaculty").setParameter("faculty", faculty).setParameter("role", role));
  }

  public List<User> findByRoleAndUniversity(UserRole role, University university) {
    return list(namedQuery("User.findByRoleAndUniversity").setParameter("university", university).setParameter("role", role));
  }

  public List<User> findByRoleAndCompany(UserRole role, Company company) {
    return list(namedQuery("User.findByRoleAndCompany").setParameter("company", company).setParameter("role", role));
  }

  public List<User> findAll() {
    return list(namedQuery("User.findAll"));
  }

  public void delete(User user) {
    currentSession().delete(user);
  }
}

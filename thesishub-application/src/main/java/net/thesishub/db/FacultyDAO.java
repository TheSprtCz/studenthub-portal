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
package net.thesishub.db;

import java.util.List;

import org.hibernate.SessionFactory;

import io.dropwizard.hibernate.AbstractDAO;
import net.thesishub.core.Faculty;
import net.thesishub.core.University;

/**
 * Data(base) Access Object for Faculty objects.
 * 
 * @author sbunciak
 * @since 1.0
 */
public class FacultyDAO extends AbstractDAO<Faculty> {

  public FacultyDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Faculty update(Faculty faculty) {
    currentSession().clear();
    return persist(faculty);
  }

  public Faculty create(Faculty faculty) {
    return persist(faculty);
  }

  public Faculty findById(Long id) {
    return get(id);
  }

  @SuppressWarnings("unchecked")
  public List<Faculty> findAll() {
    return list(namedQuery("Faculty.findAll"));
  }

  @SuppressWarnings("unchecked")
  public List<Faculty> findAllByUniversity(University university) {
    return list(namedQuery("Faculty.findByUniversity").setParameter("university", university));
  }

  public void delete(Faculty faculty) {
    currentSession().delete(faculty);
  }

  public List<Faculty> search(University university, String text) {
    return list(query("SELECT f FROM Faculty f WHERE lower(f.name) LIKE :pattern AND f.university = :university")
        .setParameter("pattern", "%" + text.toLowerCase() + "%").setParameter("university", university));
  }
}

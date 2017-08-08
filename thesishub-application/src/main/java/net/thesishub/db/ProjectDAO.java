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
import net.thesishub.core.Company;
import net.thesishub.core.Faculty;
import net.thesishub.core.Project;
import net.thesishub.core.Topic;
import net.thesishub.core.User;

/**
 * Data(base) Access Object for Project objects.
 * 
 * @author phala
 * @since 1.1
 */
public class ProjectDAO extends AbstractDAO<Project> {

  public ProjectDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Project update(Project project) {
    currentSession().clear();
    return persist(project);
  }
  
  public Project create(Project project) {
    return persist(project);
  }

  public Project findById(long id) {
    return get(id);
  }

  public List<Project> findAll() {
    return list(namedQuery("Project.findAll"));
  }

  public List<Project> findByTopic(Topic topic) {
    return list(namedQuery("Project.findByTopic").setParameter("topic", topic));
  }

  public List<Project> findByCompany(Company company) {
    return list(namedQuery("Project.findByCompany").setParameter("company", company));
  }

  public List<Project> findByFaculty(Faculty faculty) {
    return list(namedQuery("Project.findByFaculty").setParameter("faculty", faculty));
  }

  public List<Project> findByCreator(User creator) {
    return list(namedQuery("Project.findByCreator").setParameter("creator", creator));
  }

  public void delete(Project activation) {
    currentSession().delete(activation);
  }
}

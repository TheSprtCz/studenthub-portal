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

import cz.studenthub.core.Faculty;
import cz.studenthub.core.Topic;
import cz.studenthub.core.TopicApplication;
import cz.studenthub.core.User;
import io.dropwizard.hibernate.AbstractDAO;

/**
 * Data(base) Access Object for TopicApplication objects.
 * 
 * @author sbunciak
 * @since 1.0
 */
public class TopicApplicationDAO extends AbstractDAO<TopicApplication> {

  public TopicApplicationDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public TopicApplication createOrUpdate(TopicApplication ta) {
    return persist(ta);
  }

  public TopicApplication findById(Long id) {
    return get(id);
  }

  public List<TopicApplication> findAll() {
    return list(namedQuery("TopicApplication.findAll"));
  }

  public List<TopicApplication> findByFaculty(Faculty faculty) {
    return list(namedQuery("TopicApplication.findByFaculty").setParameter("faculty", faculty));
  }

  public List<TopicApplication> findByTopic(Topic topic) {
    return list(namedQuery("TopicApplication.findByTopic").setParameter("topic", topic));
  }

  public List<TopicApplication> findByStudent(User student) {
    return list(namedQuery("TopicApplication.findByStudent").setParameter("student", student));
  }

  public List<TopicApplication> findBySupervisor(User supervisor) {
    return list(namedQuery("TopicApplication.findBySupervisor").setParameter("supervisor", supervisor));
  }

  public void delete(TopicApplication ta) {
    currentSession().delete(ta);
  }
}

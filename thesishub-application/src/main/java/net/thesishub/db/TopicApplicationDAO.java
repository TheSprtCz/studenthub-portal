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

import java.util.Collection;
import java.util.List;

import org.hibernate.SessionFactory;

import io.dropwizard.hibernate.AbstractDAO;
import net.thesishub.core.Faculty;
import net.thesishub.core.Topic;
import net.thesishub.core.TopicApplication;
import net.thesishub.core.User;

/**
 * Data(base) Access Object for TopicApplication objects.
 * 
 * @author sbunciak
 * @since 1.0
 */
public class TopicApplicationDAO extends GenericDAO<TopicApplication, Long> {

  public TopicApplicationDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
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

  public List<TopicApplication> findByTopics(Collection<Topic> topics) {
    return list(namedQuery("TopicApplication.findByTopics").setParameter("topics", topics));
  }

  public List<TopicApplication> findByStudent(User student) {
    return list(namedQuery("TopicApplication.findByStudent").setParameter("student", student));
  }

  public List<TopicApplication> findByLeader(User leader) {
    return list(namedQuery("TopicApplication.findByLeader").setParameter("leader", leader));
  }

  public List<TopicApplication> findBySupervisor(User supervisor) {
    return list(namedQuery("TopicApplication.findBySupervisor").setParameter("supervisor", supervisor));
  }

}

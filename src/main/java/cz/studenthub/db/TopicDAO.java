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

import cz.studenthub.core.Topic;
import io.dropwizard.hibernate.AbstractDAO;

/**
 * Data(base) Access Object for Topic objects.
 * 
 * @author sbunciak
 * @since 1.0
 */
public class TopicDAO extends AbstractDAO<Topic> {

  public TopicDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Topic createOrUpdate(Topic t) {
    return persist(t);
  }

  public Topic findById(Long id) {
    return get(id);
  }

  public List<Topic> findAll() {
    return list(namedQuery("Topic.findAll"));
  }

  public void delete(Topic t) {
    currentSession().delete(t);
  }
}
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
import net.thesishub.core.TopicDegree;

/**
 * Data(base) Access Object for TopicDegree objects.
 * 
 * @author phala
 * @since 1.1
 */
public class TopicDegreeDAO extends AbstractDAO<TopicDegree> {

  public TopicDegreeDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public TopicDegree update(TopicDegree degree) {
    currentSession().clear();
    return persist(degree);
  }
  
  public TopicDegree create(TopicDegree degree) {
    return persist(degree);
  }
  
  public TopicDegree findByName(String name) {
    return get(name);
  }
  
  @SuppressWarnings("unchecked")
  public List<TopicDegree> findAll() {
    return list(namedQuery("TopicDegree.findAll"));
  }
  
  public void delete(TopicDegree degree) {
    currentSession().delete(degree);
  }

}

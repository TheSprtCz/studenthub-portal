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

import cz.studenthub.core.Activation;
import cz.studenthub.core.User;
import io.dropwizard.hibernate.AbstractDAO;

/**
 * Data(base) Access Object for Company objects.
 * 
 * @author sbunciak
 * @since 1.0
 */
public class ActivationDAO extends AbstractDAO<Activation> {

  public ActivationDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Activation update(Activation activation) {
    currentSession().clear();
    return persist(activation);
  }
  
  public Activation create(Activation activation) {
    return persist(activation);
  }

  public Activation findById(long id) {
    return get(id);
  }

  public Activation findByUser(User user) {
    return uniqueResult(namedQuery("Activation.findByUser").setParameter("user", user));
  }

  public List<Activation> findAll() {
    return list(namedQuery("Activation.findAll"));
  }

  public void delete(Activation activation) {
    currentSession().delete(activation);
  }

}

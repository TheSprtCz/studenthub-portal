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

import net.thesishub.core.Activation;
import net.thesishub.core.ActivationType;
import net.thesishub.core.User;

/**
 * Data(base) Access Object for Activation objects.
 * 
 * @author phala
 * @since 1.0
 */
public class ActivationDAO extends GenericDAO<Activation, Long> {

  public ActivationDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Activation findByUser(User user) {
    return uniqueResult(namedQuery("Activation.findByUser").setParameter("user", user));
  }

  public Activation findByUserAndType(User user, ActivationType type) {
    return uniqueResult(namedQuery("Activation.findByUserAndType").setParameter("user", user).setParameter("type", type));
  }

  public List<Activation> findAll() {
    return list(namedQuery("Activation.findAll"));
  }

}

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

import net.thesishub.core.Notification;
import net.thesishub.core.User;

/**
 * Data(base) Access Object for Notification objects.
 * 
 * @author phala
 * @since 1.1
 */
public class NotificationDAO extends GenericDAO<Notification, Long> {

  public NotificationDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public List<Notification> findByUser(User user) {
    return list(namedQuery("Notification.findByUser").setParameter("user", user));
  }

}

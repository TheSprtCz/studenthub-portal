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
import io.dropwizard.hibernate.AbstractDAO;

/**
 * Data(base) Access Object for Company objects.
 * 
 * @author sbunciak
 * @since 1.0
 */
public class CompanyDAO extends AbstractDAO<Company> {

  public CompanyDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Company createOrUpdate(Company c) {
    return persist(c);
  }
  
  public Company findById(Long id) {
    return get(id);
  }
  
  public List<Company> findAll() {
    return list(namedQuery("Company.findAll"));
  }
  
  public void delete(Company c) {
    currentSession().delete(c);
  }
}

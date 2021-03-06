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

  public Company update(Company company) {
    currentSession().clear();
    return persist(company);
  }
  
  public Company create(Company company) {
    return persist(company);
  }
  
  public Company findById(Long id) {
    return get(id);
  }
  
  public List<Company> findAll() {
    return list(namedQuery("Company.findAll"));
  }
  
  public void delete(Company company) {
    currentSession().delete(company);
  }

}

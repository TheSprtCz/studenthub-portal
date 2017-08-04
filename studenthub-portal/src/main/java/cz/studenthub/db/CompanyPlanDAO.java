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

import cz.studenthub.core.CompanyPlan;
import io.dropwizard.hibernate.AbstractDAO;

/**
 * Data(base) Access Object for CompanyPlan objects.
 * 
 * @author phala
 * @since 1.1
 */
public class CompanyPlanDAO extends AbstractDAO<CompanyPlan> {

  public CompanyPlanDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public CompanyPlan update(CompanyPlan companyPlan) {
    currentSession().clear();
    return persist(companyPlan);
  }
  
  public CompanyPlan create(CompanyPlan companyPlan) {
    return persist(companyPlan);
  }
  
  public CompanyPlan findByName(String name) {
    return get(name);
  }
  
  public List<CompanyPlan> findAll() {
    return list(namedQuery("CompanyPlan.findAll"));
  }
  
  public void delete(CompanyPlan companyPlan) {
    currentSession().delete(companyPlan);
  }

}

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
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.persister.collection.CollectionPropertyNames;
import org.hibernate.sql.JoinType;

import io.dropwizard.hibernate.AbstractDAO;
import net.thesishub.core.Company;
import net.thesishub.core.Topic;
import net.thesishub.core.User;

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

  public Topic update(Topic topic) {
    currentSession().clear();
    return persist(topic);
  }
  
  public Topic create(Topic topic) {
    return persist(topic);
  }

  public Topic findById(Long id) {
    return get(id);
  }

  @SuppressWarnings("unchecked")
  public List<Topic> findBySupervisor(User supervisor) {
    return list(namedQuery("Topic.findBySupervisor").setParameter("supervisor", supervisor));
  }

  @SuppressWarnings("unchecked")
  public List<Topic> findByCreator(User creator) {
    return list(namedQuery("Topic.findByCreator").setParameter("creator", creator));
  }

  @SuppressWarnings("unchecked")
  public List<Topic> findByTag(String tag) {
    return list(namedQuery("Topic.findByTag").setParameter("tag", tag));
  }

  @SuppressWarnings("unchecked")
  public List<Topic> findByCompany(Company company) {
    return list(namedQuery("Topic.findByCompany").setParameter("company", company));
  }

  @SuppressWarnings("unchecked")
  public List<Topic> findHighlighted() {
    return list(namedQuery("Topic.findHighlighted"));
  }

  @SuppressWarnings("unchecked")
  public List<Topic> findAllOrdered(int maxResults) {
    return list(namedQuery("Topic.findAllOrdered").setMaxResults(maxResults));
  }

  public int countByCompany(Company company) {
    return ((Number) namedQuery("Topic.countByCompany").setParameter("company", company).getSingleResult()).intValue();
  }

  @SuppressWarnings("unchecked")
  public List<Topic> findAll() {
    return list(namedQuery("Topic.findAll"));
  }

  public List<Topic> search(String text, Set<Long> companies, Set<String> degrees) {
    
    String pattern = "%" + text + "%";

    Criteria criteria = criteria().createAlias("tags", "tag", JoinType.RIGHT_OUTER_JOIN)
        .add(Restrictions.or(Restrictions.ilike("title", pattern),
            Restrictions.ilike("shortAbstract", pattern),
            Restrictions.ilike("secondaryTitle", pattern),
            Restrictions.ilike("secondaryDescription", pattern),
            Restrictions.ilike("description", pattern),
            Restrictions.eq("tag." + CollectionPropertyNames.COLLECTION_ELEMENTS, text).ignoreCase()))
        .add(Restrictions.eq("enabled", true));

    if (companies.size() > 0) {
      Disjunction companyFilter = Restrictions.or();
      for (Long id : companies) {
        companyFilter.add(Restrictions.eq("cmp.id", id));
      }
      criteria.createAlias("creator.company", "cmp").add(companyFilter);
    }

    if (degrees.size() > 0) {
      Disjunction degreeFilter = Restrictions.or();
      for (String degree : degrees) {
        degreeFilter.add(Restrictions.eq("degree.name", degree));
      }
      criteria.setFetchMode("degrees", FetchMode.JOIN).createAlias("degrees", "degree").add(degreeFilter);
    }

    criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

    return list(criteria);
  }

  public void delete(Topic topic) {
    currentSession().delete(topic);
  }
}

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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.SessionFactory;
import io.dropwizard.hibernate.AbstractDAO;
import net.thesishub.core.Company;
import net.thesishub.core.Topic;
import net.thesishub.core.TopicDegree;
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

  private Predicate ilike(CriteriaBuilder builder, Expression<String> expression, String pattern) {
    return builder.like(builder.lower(expression), pattern);
  }
  
  public List<Topic> search(String text, Set<Long> companies, Set<String> degrees) {
    
    // compare everything in lowercase
    text = text.toLowerCase(); 
    String pattern = "%" + text + "%";

    CriteriaBuilder builder = currentSession().getCriteriaBuilder();
    CriteriaQuery<Topic> query = criteriaQuery().distinct(true);
    Root<Topic> root = query.from(Topic.class);

    Join<Topic, String> tag = root.join("tags", JoinType.INNER);

    Predicate where = builder.conjunction();
    
    // Only enabled
    where = builder.and(where, builder.isTrue(root.get("enabled")));

    // Filtering based on attributes of Topic
    where = builder.and(where, builder.or(
        ilike(builder, root.get("shortAbstract"), pattern),
        ilike(builder, root.get("secondaryTitle"), pattern),
        ilike(builder, root.get("secondaryDescription"), pattern),
        ilike(builder, root.get("description"), pattern),
        builder.equal(builder.lower(tag), text)
    ));
    
    // Filtering by companies
    if (companies.size() > 0) {
        Join<User, Company> company = root.join("creator").join("company");

        Predicate companyFilter = builder.disjunction();
        for (Long id : companies) {
          companyFilter = builder.or(companyFilter, builder.equal(company.get("id"), id));
        }

        where = builder.and(where, companyFilter);
    }
    
    // Filtering by degrees
    if (degrees.size() > 0) {
        Join<Topic, TopicDegree> degree = root.join("degrees");

        Predicate degreeFilter = builder.disjunction();
        for (String id : degrees) {
          degreeFilter = builder.or(degreeFilter, builder.equal(degree.get("name"), id));
        }

        where = builder.and(where, degreeFilter);
    }

    // Select all fields from Topic e.g Topic object
    query.select(root);

    // Filter based on the constructed query
    query.where(where);

    return list(query);
  }

  public void delete(Topic topic) {
    currentSession().delete(topic);
  }
}

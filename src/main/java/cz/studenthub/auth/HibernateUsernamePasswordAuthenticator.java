/*******************************************************************************
 *     Copyright (C) 2017  Stefan Bunciak
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
package cz.studenthub.auth;

import static java.util.Objects.requireNonNull;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.AbstractUsernamePasswordAuthenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;

import cz.studenthub.core.User;
import io.dropwizard.hibernate.UnitOfWork;

/**
 * Pac4j authenticator which uses Hibernate to query database for user credentials.
 * 
 * @author sbunciak
 * @since 1.0
 */
public class HibernateUsernamePasswordAuthenticator extends AbstractUsernamePasswordAuthenticator {

  private static final String QUERY_STRING = "SELECT u FROM User u WHERE u.email = :email";
  
  private SessionFactory sessionFactory;

  public HibernateUsernamePasswordAuthenticator() {}
  
  public HibernateUsernamePasswordAuthenticator(SessionFactory sessionFactory) {
    setSessionFactory(sessionFactory);
    setPasswordEncoder(new StudentHubPasswordEncoder());
  }

  @Override
  @UnitOfWork
  public void validate(UsernamePasswordCredentials credentials, WebContext context) throws HttpAction {
    
    Query q = currentSession().createQuery(QUERY_STRING).setParameter("email", credentials.getUsername());
    User u = uniqueResult(q);
    
    if (u == null || !getPasswordEncoder().matches(credentials.getPassword(), u.getPassword())) {
      // user not found OR credentials doesn't match
      throw new CredentialsException("Bad credentials.");
    } else {
      // OK
      credentials.setUserProfile(createProfile(u));
    }
  }
  
  private StudentHubProfile createProfile(User user) {
    StudentHubProfile profile = new StudentHubProfile();
    profile.setId(user.getId());
    profile.addAttribute("username", user.getUsername());
    profile.addAttribute("email", user.getEmail());
    profile.addAttribute("display_name", user.getName());
    profile.setRoles(user.getRoles());
    profile.setRemembered(true);
    return profile;
  }
  
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = requireNonNull(sessionFactory);
  }
  
  /**
   * Returns the current {@link Session}.
   *
   * @return the current session
   */
  protected Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  /**
   * Convenience method to return a single instance that matches the query, or
   * null if the query returns no results.
   *
   * @param query
   *          the query to run
   * @return the single result or {@code null}
   * @throws HibernateException
   *           if there is more than one matching result
   * @see Query#uniqueResult()
   */
  protected User uniqueResult(Query query) throws HibernateException {
    return (User) requireNonNull(query).uniqueResult();
  }
}

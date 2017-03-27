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
package cz.studenthub;

import static cz.studenthub.auth.Consts.ADMIN;
import static cz.studenthub.auth.Consts.BASIC_AUTH;
import static cz.studenthub.auth.Consts.COMPANY_REP;
import static cz.studenthub.auth.Consts.JWT_AUTH;
import static cz.studenthub.auth.Consts.STUDENT;
import static cz.studenthub.auth.Consts.SUPERVISOR;
import static cz.studenthub.auth.Consts.TECH_LEADER;

import javax.ws.rs.core.HttpHeaders;

import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.hibernate.SessionFactory;
import org.pac4j.core.config.Config;
import org.pac4j.http.client.direct.DirectBasicAuthClient;
import org.pac4j.http.client.direct.DirectFormClient;
import org.pac4j.http.client.direct.HeaderClient;
import org.pac4j.jax.rs.features.Pac4JSecurityFeature;
import org.pac4j.jax.rs.jersey.features.Pac4JValueFactoryProvider;
import org.pac4j.jax.rs.servlet.features.ServletJaxRsContextFactoryProvider;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;

import com.codahale.metrics.health.HealthCheck;

import cz.studenthub.auth.HibernateUsernamePasswordAuthenticator;
import cz.studenthub.auth.StudentHubAuthorizer;
import cz.studenthub.auth.StudentHubPasswordEncoder;
import cz.studenthub.core.Company;
import cz.studenthub.core.Faculty;
import cz.studenthub.core.Topic;
import cz.studenthub.core.TopicApplication;
import cz.studenthub.core.University;
import cz.studenthub.core.User;
import cz.studenthub.core.UserRole;
import cz.studenthub.db.CompanyDAO;
import cz.studenthub.db.FacultyDAO;
import cz.studenthub.db.TopicApplicationDAO;
import cz.studenthub.db.TopicDAO;
import cz.studenthub.db.UniversityDAO;
import cz.studenthub.db.UserDAO;
import cz.studenthub.health.StudentHubHealthCheck;
import cz.studenthub.resources.CompanyResource;
import cz.studenthub.resources.FacultyResource;
import cz.studenthub.resources.LoginResource;
import cz.studenthub.resources.TagResource;
import cz.studenthub.resources.TopicApplicationResource;
import cz.studenthub.resources.TopicResource;
import cz.studenthub.resources.UniversityResource;
import cz.studenthub.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Dropwizard application entry point.
 * 
 * @author sbunciak
 * @since 1.0
 */
public class StudentHubApplication extends Application<StudentHubConfiguration> {

  /*
   * Hibernate bundle initialization
   */
  private final HibernateBundle<StudentHubConfiguration> hibernate = new HibernateBundle<StudentHubConfiguration>(
      // list of entities
      User.class, Topic.class, TopicApplication.class, Company.class, University.class, Faculty.class) {

    @Override
    public DataSourceFactory getDataSourceFactory(StudentHubConfiguration configuration) {
      return configuration.getDataSourceFactory();
    }
  };

  /*
   * Main method
   */
  public static void main(final String[] args) throws Exception {
    new StudentHubApplication().run(args);
  }

  @Override
  public String getName() {
    return "Student Hub";
  }

  @Override
  public void initialize(final Bootstrap<StudentHubConfiguration> bootstrap) {
    bootstrap.addBundle(hibernate);
    bootstrap.addBundle(new AssetsBundle("/assets/", "/", "index.html"));

    // 1. load conf. yaml from classpath
    // 2. enable env. var substitutions
    bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(new ResourceConfigurationSourceProvider(),
        new EnvironmentVariableSubstitutor(false)));

    // enable database migrations
    bootstrap.addBundle(new MigrationsBundle<StudentHubConfiguration>() {
      @Override
      public DataSourceFactory getDataSourceFactory(StudentHubConfiguration configuration) {
        return configuration.getDataSourceFactory();
      }
    });
  }

  @Override
  public void run(final StudentHubConfiguration configuration, final Environment environment) {

    configurePac4j(environment);

    // initialize DAOs
    final CompanyDAO companyDao = new CompanyDAO(hibernate.getSessionFactory());
    final UniversityDAO uniDao = new UniversityDAO(hibernate.getSessionFactory());
    final FacultyDAO facDao = new FacultyDAO(hibernate.getSessionFactory());
    final UserDAO userDao = new UserDAO(hibernate.getSessionFactory());
    final TopicDAO topicDao = new TopicDAO(hibernate.getSessionFactory());
    final TopicApplicationDAO taDao = new TopicApplicationDAO(hibernate.getSessionFactory());

    // enable session manager
    environment.servlets().setSessionHandler(new SessionHandler());

    // register resource classes (REST Endpoints)
    environment.jersey().register(new CompanyResource(companyDao, userDao, topicDao));
    environment.jersey().register(new UniversityResource(uniDao, facDao));
    environment.jersey().register(new FacultyResource(facDao, userDao));
    environment.jersey().register(new UserResource(userDao, topicDao, taDao));
    environment.jersey().register(new TopicResource(topicDao, taDao, userDao));
    environment.jersey().register(new TopicApplicationResource(taDao, userDao));
    environment.jersey().register(new LoginResource());
    environment.jersey().register(new TagResource(userDao, topicDao));

    // since routing is achieved on client side we need to catch 404 and
    // redirect to index.html - handle 404 on client as well (this makes SPA
    // routing possible)
    final ErrorPageErrorHandler epeh = new ErrorPageErrorHandler();
    epeh.addErrorPage(404, "/index.html");
    environment.getApplicationContext().setErrorHandler(epeh);

    // healthcheck
    HealthCheck hc = new UnitOfWorkAwareProxyFactory(hibernate).create(StudentHubHealthCheck.class, UserDAO.class,
        userDao);
    environment.healthChecks().register("admin", hc);
  }

  private void configurePac4j(Environment environment) {
    // enable transactions in hibernate authenticator
    HibernateUsernamePasswordAuthenticator hibernateAuth = new UnitOfWorkAwareProxyFactory(hibernate)
        .create(HibernateUsernamePasswordAuthenticator.class, SessionFactory.class, hibernate.getSessionFactory());

    JwtAuthenticator jwtAuth = new JwtAuthenticator();
    jwtAuth.setSignatureConfiguration(new SecretSignatureConfiguration(StudentHubPasswordEncoder.DEFAULT_SECRET));

    // create clients (= ways of authenticating)
    DirectFormClient formClient = new DirectFormClient(hibernateAuth);
    DirectBasicAuthClient basicAuthClient = new DirectBasicAuthClient(hibernateAuth);
    basicAuthClient.setName(BASIC_AUTH);
    HeaderClient jwtClient = new HeaderClient(HttpHeaders.AUTHORIZATION, LoginResource.BEARER_PREFFIX, jwtAuth);
    jwtClient.setName(JWT_AUTH);

    Config pac4jConfig = new Config(formClient, basicAuthClient, jwtClient);
    pac4jConfig.getClients().setDefaultClient(basicAuthClient);

    // setup custom authorizers for role based access
    pac4jConfig.addAuthorizer(ADMIN, new StudentHubAuthorizer(UserRole.ADMIN));
    pac4jConfig.addAuthorizer(STUDENT, new StudentHubAuthorizer(UserRole.STUDENT));
    pac4jConfig.addAuthorizer(TECH_LEADER, new StudentHubAuthorizer(UserRole.TECH_LEADER));
    pac4jConfig.addAuthorizer(COMPANY_REP, new StudentHubAuthorizer(UserRole.COMPANY_REP));
    pac4jConfig.addAuthorizer(SUPERVISOR, new StudentHubAuthorizer(UserRole.AC_SUPERVISOR));

    environment.jersey().register(new ServletJaxRsContextFactoryProvider(pac4jConfig));
    environment.jersey().register(new Pac4JSecurityFeature(pac4jConfig));
    environment.jersey().register(new Pac4JValueFactoryProvider.Binder());
  }
}

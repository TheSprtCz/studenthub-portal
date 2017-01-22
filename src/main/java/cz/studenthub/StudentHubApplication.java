package cz.studenthub;

import org.eclipse.jetty.server.session.SessionHandler;
import org.hibernate.SessionFactory;
import org.pac4j.core.config.Config;
import org.pac4j.http.client.direct.DirectBasicAuthClient;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.jax.rs.features.Pac4JSecurityFeature;
import org.pac4j.jax.rs.jersey.features.Pac4JValueFactoryProvider;
import org.pac4j.jax.rs.pac4j.JaxRsCallbackUrlResolver;
import org.pac4j.jax.rs.servlet.features.ServletJaxRsContextFactoryProvider;

import com.codahale.metrics.health.HealthCheck;

import cz.studenthub.auth.HibernateUsernamePasswordAuthenticator;
import cz.studenthub.auth.StudentHubAuthorizer;
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
import cz.studenthub.resources.TopicApplicationResource;
import cz.studenthub.resources.TopicResource;
import cz.studenthub.resources.UniversityResource;
import cz.studenthub.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
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

    // 1. load conf. yaml from classpath
    // 2. enable env. var substitutions
    bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(new ResourceConfigurationSourceProvider(),
        new EnvironmentVariableSubstitutor(false)));
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
    environment.jersey().register(new CompanyResource(companyDao));
    environment.jersey().register(new UniversityResource(uniDao));
    environment.jersey().register(new FacultyResource(facDao));
    environment.jersey().register(new UserResource(userDao));
    environment.jersey().register(new TopicResource(topicDao));
    environment.jersey().register(new TopicApplicationResource(taDao));

    // healthcheck
    HealthCheck hc = new UnitOfWorkAwareProxyFactory(hibernate).create(StudentHubHealthCheck.class, UserDAO.class,
        userDao);
    environment.healthChecks().register("admin", hc);
  }

  private void configurePac4j(Environment environment) {
    // enable transactions in authenticator
    HibernateUsernamePasswordAuthenticator authenticator = new UnitOfWorkAwareProxyFactory(hibernate)
        .create(HibernateUsernamePasswordAuthenticator.class, SessionFactory.class, hibernate.getSessionFactory());

    FormClient formClient = new FormClient("/login", authenticator);
    DirectBasicAuthClient basicAuthClient = new DirectBasicAuthClient(authenticator);

    Config pac4jConfig = new Config("/callback", formClient, basicAuthClient);
    pac4jConfig.getClients().setCallbackUrlResolver(new JaxRsCallbackUrlResolver());
    pac4jConfig.getClients().setDefaultClient(basicAuthClient);

    // setup custom authorizers for role based access
    pac4jConfig.addAuthorizer("isAdmin", new StudentHubAuthorizer(UserRole.ADMIN));
    pac4jConfig.addAuthorizer("isStudent", new StudentHubAuthorizer(UserRole.STUDENT));
    pac4jConfig.addAuthorizer("isTechLeader", new StudentHubAuthorizer(UserRole.TECH_LEADER));
    pac4jConfig.addAuthorizer("isCompanyRep", new StudentHubAuthorizer(UserRole.COMPANY_REP));
    pac4jConfig.addAuthorizer("isSupervisor", new StudentHubAuthorizer(UserRole.AC_SUPERVISOR));

    environment.jersey().register(new ServletJaxRsContextFactoryProvider(pac4jConfig));
    environment.jersey().register(new Pac4JSecurityFeature(pac4jConfig));
    environment.jersey().register(new Pac4JValueFactoryProvider.Binder());
  }
}

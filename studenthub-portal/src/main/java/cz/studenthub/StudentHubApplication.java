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

import java.util.List;

import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.servlet.ServletProperties;
import org.hibernate.SessionFactory;
import com.codahale.metrics.health.HealthCheck;
import com.google.common.collect.Lists;

import cz.studenthub.api.DAOBinder;
import cz.studenthub.auth.BasicAuthenticator;
import cz.studenthub.auth.JwtCookieAuthFilter;
import cz.studenthub.auth.StudentHubAuthorizer;
import cz.studenthub.auth.TokenAuthenticator;
import cz.studenthub.core.Activation;
import cz.studenthub.core.Company;
import cz.studenthub.core.CompanyPlan;
import cz.studenthub.core.Faculty;
import cz.studenthub.core.Project;
import cz.studenthub.core.Task;
import cz.studenthub.core.Topic;
import cz.studenthub.core.TopicApplication;
import cz.studenthub.core.University;
import cz.studenthub.core.User;
import cz.studenthub.db.UserDAO;
import cz.studenthub.health.StudentHubHealthCheck;
import cz.studenthub.resources.CompanyPlanResource;
import cz.studenthub.resources.CompanyResource;
import cz.studenthub.resources.FacultyResource;
import cz.studenthub.resources.LoginResource;
import cz.studenthub.resources.ProjectResource;
import cz.studenthub.resources.RegistrationResource;
import cz.studenthub.resources.TagResource;
import cz.studenthub.resources.TaskResource;
import cz.studenthub.resources.TopicApplicationResource;
import cz.studenthub.resources.TopicResource;
import cz.studenthub.resources.UniversityResource;
import cz.studenthub.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.Authorizer;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.auth.chained.ChainedAuthFilter;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.winterly.dropwizard.hk2bundle.HK2Bundle;

/**
 * Dropwizard application entry point.
 * 
 * @author sbunciak
 * @since 1.0
 */
public class StudentHubApplication extends Application<StudentHubConfiguration> {

  public static final String NAME = "Student Hub";
  
  /*
   * Hibernate bundle initialization
   */
  private final HibernateBundle<StudentHubConfiguration> hibernate = new HibernateBundle<StudentHubConfiguration>(
      // list of entities
      User.class, Topic.class, TopicApplication.class, Company.class, University.class, Faculty.class, Task.class,
      Activation.class, CompanyPlan.class, Project.class) {

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
    return NAME;
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

    bootstrap.addBundle(new HK2Bundle(this));
  }

  @Override
  public void run(final StudentHubConfiguration configuration, final Environment environment) {

    // Load DAOs into HK2
    ServiceLocator locator = (ServiceLocator) environment.getApplicationContext().getAttribute(ServletProperties.SERVICE_LOCATOR);
    ServiceLocatorUtilities.bind(locator, new UnitOfWorkAwareProxyFactory(hibernate)
        .create(DAOBinder.class, SessionFactory.class, hibernate.getSessionFactory()));

    // enable session manager
    environment.servlets().setSessionHandler(new SessionHandler());

    // register resource classes (REST Endpoints)
    environment.jersey().register(new CompanyResource());
    environment.jersey().register(new UniversityResource());
    environment.jersey().register(new FacultyResource());
    environment.jersey().register(new UserResource());
    environment.jersey().register(new TopicResource());
    environment.jersey().register(new TopicApplicationResource());
    environment.jersey().register(new TaskResource());
    environment.jersey().register(new LoginResource(configuration.getJwtSecret()));
    environment.jersey().register(new RegistrationResource(configuration.getSmtpConfig()));
    environment.jersey().register(new TagResource());
    environment.jersey().register(new CompanyPlanResource());
    environment.jersey().register(new ProjectResource());

    // set up auth
    configureAuth(configuration, environment, locator);

    // since routing is achieved on client side we need to catch 404 and
    // redirect to index.html - handle 404 on client as well (this makes SPA
    // routing possible)
    final ErrorPageErrorHandler epeh = new ErrorPageErrorHandler();
    epeh.addErrorPage(404, "/index.html");
    environment.getApplicationContext().setErrorHandler(epeh);

    // healthcheck
    HealthCheck hc = new UnitOfWorkAwareProxyFactory(hibernate)
        .create(StudentHubHealthCheck.class);

    environment.healthChecks().register("admin", hc);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void configureAuth(StudentHubConfiguration configuration, Environment environment, ServiceLocator locator) {
    // Since HK2 doesn't work with UnitOfAwareProxy, we have to retrieve instance from HK2 manually 
    UserDAO dao = locator.getService(UserDAO.class);

    Class<?>[] types = {UserDAO.class, String.class};
    Object[] args = {dao, configuration.getJwtSecret()};
    
    TokenAuthenticator tokenAuth = new UnitOfWorkAwareProxyFactory(hibernate).create(TokenAuthenticator.class, types, args);
    Authorizer<User> authorizer = new StudentHubAuthorizer();

    AuthFilter<String, User> oauthCredentialAuthFilter = new OAuthCredentialAuthFilter.Builder<User>()
        .setAuthenticator(tokenAuth)
        .setAuthorizer(authorizer)
        .setPrefix("Bearer")
        .setRealm(this.getName())
        .buildAuthFilter();
    
    AuthFilter<String, User> cookieCredentialAuthFilter = new JwtCookieAuthFilter.Builder<User>()
        .setCookieName(LoginResource.COOKIE_NAME)
        .setAuthenticator(tokenAuth)
        .setAuthorizer(authorizer)
        .setRealm(this.getName())
        .buildAuthFilter();

    List<AuthFilter> filters = Lists.newArrayList(oauthCredentialAuthFilter, cookieCredentialAuthFilter);

    if (configuration.isBasicAuthEnabled()) {
      BasicAuthenticator basicAuth = new UnitOfWorkAwareProxyFactory(hibernate).create(BasicAuthenticator.class,
          UserDAO.class, dao);
      AuthFilter<BasicCredentials, User> basicCredentialAuthFilter = new BasicCredentialAuthFilter.Builder<User>()
          .setAuthenticator(basicAuth)
          .setAuthorizer(authorizer)
          .setPrefix("Basic")
          .setRealm(this.getName())
          .buildAuthFilter();

      filters.add(basicCredentialAuthFilter);
    }

    environment.jersey().register(new AuthDynamicFeature(new ChainedAuthFilter(filters)));
    environment.jersey().register(RolesAllowedDynamicFeature.class);
    // If you want to use @Auth to inject a custom Principal type into your resource
    environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
  }

  public SessionFactory getSessionFactory() {
    return hibernate.getSessionFactory();
  }
}

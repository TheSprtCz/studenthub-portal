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
package net.thesishub;

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
import net.thesishub.api.ThesisHubBinder;
import net.thesishub.auth.BasicAuthenticator;
import net.thesishub.auth.JwtCookieAuthFilter;
import net.thesishub.auth.ThesisHubAuthorizer;
import net.thesishub.auth.TokenAuthenticator;
import net.thesishub.core.Activation;
import net.thesishub.core.Company;
import net.thesishub.core.CompanyPlan;
import net.thesishub.core.Country;
import net.thesishub.core.Faculty;
import net.thesishub.core.Project;
import net.thesishub.core.Task;
import net.thesishub.core.Topic;
import net.thesishub.core.TopicApplication;
import net.thesishub.core.TopicDegree;
import net.thesishub.core.University;
import net.thesishub.core.User;
import net.thesishub.db.UserDAO;
import net.thesishub.health.ThesisHubHealthCheck;
import net.thesishub.resources.CompanyPlanResource;
import net.thesishub.resources.CompanyResource;
import net.thesishub.resources.CountryResource;
import net.thesishub.resources.FacultyResource;
import net.thesishub.resources.LoginResource;
import net.thesishub.resources.ProjectResource;
import net.thesishub.resources.RegistrationResource;
import net.thesishub.resources.TagResource;
import net.thesishub.resources.TaskResource;
import net.thesishub.resources.TopicApplicationResource;
import net.thesishub.resources.TopicDegreeResource;
import net.thesishub.resources.TopicResource;
import net.thesishub.resources.UniversityResource;
import net.thesishub.resources.UserResource;
import net.winterly.dropwizard.hk2bundle.HK2Bundle;

/**
 * Dropwizard application entry point.
 * 
 * @author sbunciak
 * @since 1.0
 */
public class ThesisHubApplication extends Application<ThesisHubConfiguration> {

  public static final String NAME = "Thesis Hub";
  
  /*
   * Hibernate bundle initialization
   */
  private final HibernateBundle<ThesisHubConfiguration> hibernate = new HibernateBundle<ThesisHubConfiguration>(
      // list of entities
      User.class, Topic.class, TopicApplication.class, Company.class, University.class, Faculty.class, Task.class,
      Activation.class, CompanyPlan.class, Project.class, TopicDegree.class, Country.class) {

    @Override
    public DataSourceFactory getDataSourceFactory(ThesisHubConfiguration configuration) {
      return configuration.getDataSourceFactory();
    }
  };

  /*
   * Main method
   */
  public static void main(final String[] args) throws Exception {
    new ThesisHubApplication().run(args);
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public void initialize(final Bootstrap<ThesisHubConfiguration> bootstrap) {
    bootstrap.addBundle(hibernate);
    bootstrap.addBundle(new AssetsBundle("/assets/", "/", "index.html"));

    // 1. load conf. yaml from classpath
    // 2. enable env. var substitutions
    bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(new ResourceConfigurationSourceProvider(),
        new EnvironmentVariableSubstitutor(false)));

    // enable database migrations
    bootstrap.addBundle(new MigrationsBundle<ThesisHubConfiguration>() {
      @Override
      public DataSourceFactory getDataSourceFactory(ThesisHubConfiguration configuration) {
        return configuration.getDataSourceFactory();
      }
    });

    bootstrap.addBundle(HK2Bundle.builder().build());
  }

  @Override
  public void run(final ThesisHubConfiguration configuration, final Environment environment) {

    Class<?>[] types = {SessionFactory.class, ThesisHubConfiguration.class};
    Object[] args = {hibernate.getSessionFactory(), configuration};

    // Load DAOs into HK2
    ServiceLocator locator = (ServiceLocator) environment.getApplicationContext().getAttribute(ServletProperties.SERVICE_LOCATOR);
    ServiceLocatorUtilities.bind(locator, new UnitOfWorkAwareProxyFactory(hibernate)
        .create(ThesisHubBinder.class, types, args));

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
    environment.jersey().register(new RegistrationResource());
    environment.jersey().register(new TagResource());
    environment.jersey().register(new CompanyPlanResource());
    environment.jersey().register(new ProjectResource());
    environment.jersey().register(new TopicDegreeResource());
    environment.jersey().register(new CountryResource());

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
        .create(ThesisHubHealthCheck.class);

    environment.healthChecks().register("admin", hc);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void configureAuth(ThesisHubConfiguration configuration, Environment environment, ServiceLocator locator) {
    // Since HK2 doesn't work with UnitOfAwareProxy, we have to retrieve instance from HK2 manually 
    UserDAO dao = locator.getService(UserDAO.class);

    Class<?>[] types = {UserDAO.class, String.class};
    Object[] args = {dao, configuration.getJwtSecret()};
    
    TokenAuthenticator tokenAuth = new UnitOfWorkAwareProxyFactory(hibernate).create(TokenAuthenticator.class, types, args);
    Authorizer<User> authorizer = new ThesisHubAuthorizer();

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

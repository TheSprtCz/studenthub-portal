package net.thesishub;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.DropwizardTestSupport;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import net.thesishub.ThesisHubApplication;
import net.thesishub.ThesisHubConfiguration;
import net.thesishub.resources.LoginResource;

public class IntegrationTestSuite {

  public static final DropwizardTestSupport<ThesisHubConfiguration> DROPWIZARD =
            new DropwizardTestSupport<ThesisHubConfiguration>(ThesisHubApplication.class, "config-test.yml");
  public static final Logger LOG = LoggerFactory.getLogger(IntegrationTestSuite.class);
  public static JerseyClientBuilder BUILDER;

  // Superadmin credentials
  public static final String USERNAME = "superadmin@example.com";
  public static final String PASSWORD = "test";

  private static String TOKEN;

  public static final GreenMail GREENMAIL = new GreenMail(ServerSetupTest.SMTP);

  @BeforeSuite
  public void beforeClass() {
    DROPWIZARD.before();
    BUILDER = new JerseyClientBuilder(DROPWIZARD.getEnvironment()).withProperty(ClientProperties.READ_TIMEOUT, 0);
    GREENMAIL.start();
  }

  @AfterSuite(alwaysRun = true)
  public void afterClass() {
    DROPWIZARD.after();
    GREENMAIL.stop();
  }

  // Migrates database
  @Test(groups = "migrate")
  public static void migrateDatabase() {
    SessionFactory sessionFactory = getSessionFactory();
    Session session = sessionFactory.openSession();
    Transaction transaction = session.beginTransaction();
    session.doWork(connection -> {
      Database database = null;
      try {
        database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
      } catch (DatabaseException e) {
        LOG.error("Error establishing connection into DB!", e);
      }
      Liquibase liquibase;
      try {
        liquibase = new Liquibase("migrations-test.xml", new ClassLoaderResourceAccessor(), database);
        liquibase.update(new Contexts());
      } catch (LiquibaseException e) {
        LOG.error("Error migration test DB!", e);
      }
    });
    transaction.commit();
    LOG.debug("migrated");
  }

  public static Response authorizationRequest(Client client, String username, String password) {
    MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
    formData.add("username", username);
    formData.add("password", password);

    return client.target(
        String.format("http://localhost:%d/api/auth/login", DROPWIZARD.getLocalPort())).request().post(Entity.form(formData));
  }

  public static String authorize(Client client, String username, String password) {
    Response response = authorizationRequest(client, username, password);
 
    return response.getCookies().get(LoginResource.COOKIE_NAME).getValue();
  }

  public static Builder authorizedRequest(Builder target, String token) {
    return target.cookie(LoginResource.COOKIE_NAME, token);
  }

  public static Builder authorizedRequest(Builder target, Client client, String username, String password) {
    if (TOKEN == null) {
      TOKEN = authorize(client, username, password);
    }
    return target.cookie(LoginResource.COOKIE_NAME, TOKEN);
  }

  public static Builder oneTimeAuthorizedRequest(Builder target, Client client, String username, String password) {
    return target.cookie(LoginResource.COOKIE_NAME, authorize(client, username, password));
  }

  public static Builder authorizedRequest(Builder target, Client client) {
    return authorizedRequest(target, client, USERNAME, PASSWORD);
  }

  public static SessionFactory getSessionFactory() {
    return ((ThesisHubApplication) DROPWIZARD.getApplication()).getSessionFactory();
  }

}

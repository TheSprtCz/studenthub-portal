package cz.studenthub;

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

import cz.studenthub.StudentHubApplication;
import cz.studenthub.StudentHubConfiguration;
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

public class IntegrationTestSuite {

  public static final DropwizardTestSupport<StudentHubConfiguration> DROPWIZARD =
            new DropwizardTestSupport<StudentHubConfiguration>(StudentHubApplication.class, "config-test.yml");
  public static final Logger LOG = LoggerFactory.getLogger(IntegrationTestSuite.class);
  public static JerseyClientBuilder BUILDER;

  // Superadmin credentials
  public static String USERNAME = "superadmin@example.com";
  public static String PASSWORD = "test";

  private static String superToken;

  @BeforeSuite
  public void beforeClass() {
      DROPWIZARD.before();
      BUILDER = new JerseyClientBuilder(DROPWIZARD.getEnvironment()).withProperty(ClientProperties.READ_TIMEOUT, 1000);
  }

  @AfterSuite(alwaysRun = true)
  public void afterClass() {
      DROPWIZARD.after();
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
 
    return response.getCookies().get("sh-token").getValue();
  }

  public static String authorize(Client client) {
    return authorize(client, USERNAME, PASSWORD);
  }

  public static Builder authorizedRequest(Builder target, String token) {
    return target.cookie("sh-token", token);
  }

  public static Builder authorizedRequest(Builder target, Client client) {
    if (superToken == null) {
      superToken = authorize(client);
    }
    return target.cookie("sh-token", superToken);
  }

  public static SessionFactory getSessionFactory() {
    return ((StudentHubApplication) DROPWIZARD.getApplication()).getSessionFactory();
  }

}

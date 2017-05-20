package cz.studenthub;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.rules.ExternalResource;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.studenthub.StudentHubApplication;
import cz.studenthub.StudentHubConfiguration;
import cz.studenthub.integration.CompanyResourceTest;
import cz.studenthub.integration.FacultyResourceTest;
import cz.studenthub.integration.LoginResourceTest;
import cz.studenthub.integration.TagResourceTest;
import cz.studenthub.integration.TaskResourceTest;
import cz.studenthub.integration.TopicApplicationResourceTest;
import cz.studenthub.integration.TopicResourceTest;
import cz.studenthub.integration.UniversityResourceTest;
import cz.studenthub.integration.UserResourceTest;
import io.dropwizard.testing.junit.DropwizardAppRule;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

@Ignore
@RunWith(Suite.class)
@SuiteClasses({ LoginResourceTest.class, UniversityResourceTest.class, FacultyResourceTest.class,
    CompanyResourceTest.class, TagResourceTest.class, UserResourceTest.class, TopicResourceTest.class,
    TopicApplicationResourceTest.class, TaskResourceTest.class})
public class IntegrationTestSuite {

  public static DropwizardAppRule<StudentHubConfiguration> DROPWIZARD;
  public static final Logger LOG = LoggerFactory.getLogger(IntegrationTestSuite.class);


  public static ExternalResource resource;
  private static String superToken;
  
  @ClassRule
  public static TestRule chain = RuleChain.outerRule(DROPWIZARD = new DropwizardAppRule<StudentHubConfiguration>(StudentHubApplication.class, "config-test.yml"))
      .around(resource = new ExternalResource() {
          @Override
          protected void before() throws Throwable {
             migrateDatabase();
          }    
      });

  // Migrates database
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

  public static String authorize(Client client, String username, String password) {
    Response response = client.target(
        String.format("http://localhost:%d/api/auth/login", DROPWIZARD.getLocalPort()))
        .queryParam("username", "superadmin@example.com")
        .queryParam("password", "test").request().post(null);
    return response.getHeaderString("Authorization");
  }

  public static String authorize(Client client) {
    return authorize(client, "superadmin@example.com", "test");
  }

  public static Builder authorizedRequest(Builder target, String token) {
    return target.header("Authorization", token);
  }

  public static Builder authorizedRequest(Builder target, Client client) {
    if (superToken == null) {
      superToken = authorize(client);
    }
    return target.header("Authorization", superToken);
  }

  public static SessionFactory getSessionFactory() {
    return ((StudentHubApplication) DROPWIZARD.getApplication()).getSessionFactory();
  }
}

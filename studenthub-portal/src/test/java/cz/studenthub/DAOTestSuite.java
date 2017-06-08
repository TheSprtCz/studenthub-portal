package cz.studenthub;

import java.util.concurrent.Callable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.ClassRule;
import org.junit.rules.ExternalResource;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.studenthub.core.Activation;
import cz.studenthub.core.Company;
import cz.studenthub.core.CompanyPlan;
import cz.studenthub.core.Faculty;
import cz.studenthub.core.Task;
import cz.studenthub.core.Topic;
import cz.studenthub.core.TopicApplication;
import cz.studenthub.core.University;
import cz.studenthub.core.User;
import cz.studenthub.db.ActivationDAOTest;
import cz.studenthub.db.CompanyDAOTest;
import cz.studenthub.db.CompanyPlanDAOTest;
import cz.studenthub.db.FacultyDAOTest;
import cz.studenthub.db.TaskDAOTest;
import cz.studenthub.db.TopicApplicationDAOTest;
import cz.studenthub.db.TopicDAOTest;
import cz.studenthub.db.UniversityDAOTest;
import cz.studenthub.db.UserDAOTest;
import io.dropwizard.testing.junit.DAOTestRule;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

@RunWith(Suite.class)
@SuiteClasses({ CompanyDAOTest.class, FacultyDAOTest.class, TaskDAOTest.class, TopicApplicationDAOTest.class,
   TopicDAOTest.class, UniversityDAOTest.class, UserDAOTest.class, ActivationDAOTest.class, CompanyPlanDAOTest.class})
public class DAOTestSuite {

  public static DAOTestRule database;
  public static ExternalResource resource;
  protected static Logger LOG = LoggerFactory.getLogger(DAOTestSuite.class);

  @ClassRule
  public static TestRule chain = RuleChain.outerRule(database = setUp()).around(resource = new ExternalResource() {
    @Override
    protected void before() throws Throwable {
      migrateDatabase();
    }
  });

  public static DAOTestRule setUp() {
    return DAOTestRule.newBuilder().setHbm2DdlAuto("update")
        .addEntityClass(Topic.class)
        .addEntityClass(User.class)
        .addEntityClass(Company.class)
        .addEntityClass(Faculty.class)
        .addEntityClass(University.class)
        .addEntityClass(TopicApplication.class)
        .addEntityClass(Task.class)
        .addEntityClass(Activation.class)
        .addEntityClass(CompanyPlan.class).build();
  }

  public static void migrateDatabase() {
    SessionFactory sessionFactory = database.getSessionFactory();
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
  }

  // Copy of DAOTestRule#inTransaction but this is not commited but
  // rollbacked.
  public static <T> T inRollbackTransaction(Callable<T> call) {
    final Session session = database.getSessionFactory().getCurrentSession();
    final Transaction transaction = session.beginTransaction();
    try {
      final T result = call.call();
      transaction.rollback();
      return result;
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  // Copy of DAOTestRule#inTransaction but this is not commited but
  // rollbacked.
  public static void inRollbackTransaction(Runnable action) {
    inRollbackTransaction(() -> {
      action.run();
      return true;
    });
  }
}

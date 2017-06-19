package net.thesishub;

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

import io.dropwizard.testing.junit.DAOTestRule;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import net.thesishub.core.Activation;
import net.thesishub.core.Company;
import net.thesishub.core.CompanyPlan;
import net.thesishub.core.Country;
import net.thesishub.core.Faculty;
import net.thesishub.core.Notification;
import net.thesishub.core.Project;
import net.thesishub.core.Task;
import net.thesishub.core.Topic;
import net.thesishub.core.TopicApplication;
import net.thesishub.core.TopicDegree;
import net.thesishub.core.University;
import net.thesishub.core.User;
import net.thesishub.db.ActivationDAOTest;
import net.thesishub.db.CompanyDAOTest;
import net.thesishub.db.CompanyPlanDAOTest;
import net.thesishub.db.CountryDAOTest;
import net.thesishub.db.FacultyDAOTest;
import net.thesishub.db.NotificationDAOTest;
import net.thesishub.db.ProjectDAOTest;
import net.thesishub.db.TaskDAOTest;
import net.thesishub.db.TopicApplicationDAOTest;
import net.thesishub.db.TopicDAOTest;
import net.thesishub.db.TopicDegreeDAOTest;
import net.thesishub.db.UniversityDAOTest;
import net.thesishub.db.UserDAOTest;

@RunWith(Suite.class)
@SuiteClasses({ CompanyDAOTest.class, FacultyDAOTest.class, TaskDAOTest.class, TopicApplicationDAOTest.class,
   TopicDAOTest.class, UniversityDAOTest.class, UserDAOTest.class, ActivationDAOTest.class, CompanyPlanDAOTest.class,
   ProjectDAOTest.class, TopicDegreeDAOTest.class, CountryDAOTest.class, NotificationDAOTest.class})
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
        .addEntityClass(Project.class)
        .addEntityClass(TopicDegree.class)
        .addEntityClass(Country.class)
        .addEntityClass(Notification.class)
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

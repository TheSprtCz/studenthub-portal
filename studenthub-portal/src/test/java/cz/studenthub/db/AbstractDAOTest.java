package cz.studenthub.db;

import java.util.concurrent.Callable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.ClassRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.studenthub.core.Company;
import cz.studenthub.core.Faculty;
import cz.studenthub.core.Topic;
import cz.studenthub.core.TopicApplication;
import cz.studenthub.core.University;
import cz.studenthub.core.User;
import io.dropwizard.testing.junit.DAOTestRule;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public abstract class AbstractDAOTest {
  @ClassRule
  public static DAOTestRule database = DAOTestRule.newBuilder().setHbm2DdlAuto("")
                                                              .addEntityClass(Topic.class)
                                                              .addEntityClass(User.class)
                                                              .addEntityClass(Company.class)
                                                              .addEntityClass(Faculty.class)
                                                              .addEntityClass(University.class)
                                                              .addEntityClass(TopicApplication.class).build();

  protected Logger LOG = LoggerFactory.getLogger(AbstractDAOTest.class);
  @Before
  public void migrateDatabase() {
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

  // Copy of DAOTestRule#inTransaction but this it is not commited but
  // rollbacked.
  public <T> T inRollbackTransaction(Callable<T> call) {
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

  // Copy of DAOTestRule#inTransaction but this it is not commited but
  // rollbacked.
  public void inRollbackTransaction(Runnable action) {
    inRollbackTransaction(() -> {
      action.run();
      return true;
    });
  }
}

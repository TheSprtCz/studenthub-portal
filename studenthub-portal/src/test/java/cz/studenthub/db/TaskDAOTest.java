package cz.studenthub.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import cz.studenthub.DAOTestSuite;
import cz.studenthub.core.Task;
import cz.studenthub.core.TopicApplication;
import io.dropwizard.testing.junit.DAOTestRule;

public class TaskDAOTest {

  private static final DAOTestRule DATABASE = DAOTestSuite.database;
  private static TaskDAO taskDao;
  private static TopicApplicationDAO appDao;

  @BeforeClass
  public static void setUp() {
    taskDao = new TaskDAO(DATABASE.getSessionFactory());
    appDao = new TopicApplicationDAO(DATABASE.getSessionFactory());
  }

  @Test
  public void createTask() {
    DAOTestSuite.inRollbackTransaction(() -> {
      TopicApplication app = appDao.findById((long) 1);
      Task task = new Task("Create Use case diagram", false, null, app);
      Task created = taskDao.create(task);
      assertNotNull(task.getId());
      assertEquals(task, created);
      assertEquals(4, taskDao.findByTopicApplication(app).size());
    });
  }

  @Test
  public void listTasksByApplication() {
    DATABASE.inTransaction(() -> {
      TopicApplication app = appDao.findById((long) 1);
      List<Task> tasks = taskDao.findByTopicApplication(app);
      assertEquals(3, tasks.size());
    });
  }

  @Test
  public void fetchTask() {
    DATABASE.inTransaction(() -> {
      Task task = taskDao.findById((long) 4);
      assertEquals((long) 3, (long) task.getApplication().getId());
      assertTrue(task.isCompleted());
    });
  }

  @Test
  public void deleteTask() {
    DAOTestSuite.inRollbackTransaction(() -> {
      Task task = taskDao.findById((long) 1);
      taskDao.delete(task);

      List<Task> tasks = taskDao.findByTopicApplication(task.getApplication());
      assertEquals(2, tasks.size());
      assertFalse(tasks.contains(task));
    });
  }
}
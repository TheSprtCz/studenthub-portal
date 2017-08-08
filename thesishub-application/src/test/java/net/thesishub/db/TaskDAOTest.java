package net.thesishub.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import io.dropwizard.testing.junit.DAOTestRule;
import net.thesishub.DAOTestSuite;
import net.thesishub.core.Task;
import net.thesishub.core.TopicApplication;
import net.thesishub.db.TaskDAO;
import net.thesishub.db.TopicApplicationDAO;

public class TaskDAOTest {

  public static final int COUNT = 3;  
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
      assertEquals(COUNT + 1, taskDao.findByTopicApplication(app).size());
    });
  }

  @Test
  public void listTasksByApplication() {
    DATABASE.inTransaction(() -> {
      TopicApplication app = appDao.findById((long) 1);
      List<Task> tasks = taskDao.findByTopicApplication(app);
      assertEquals(COUNT, tasks.size());
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
      assertEquals(COUNT - 1, tasks.size());
      assertFalse(tasks.contains(task));
    });
  }
}
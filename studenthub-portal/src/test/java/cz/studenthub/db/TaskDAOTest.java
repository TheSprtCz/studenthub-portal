package cz.studenthub.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import cz.studenthub.core.Task;
import cz.studenthub.core.TopicApplication;

public class TaskDAOTest extends AbstractDAOTest {

  private static TaskDAO taskDao;
  private static TopicApplicationDAO appDao;

  @BeforeClass
  public static void setUp() {
    taskDao = new TaskDAO(database.getSessionFactory());
    appDao = new TopicApplicationDAO(database.getSessionFactory());
  }

  @Test
  public void createTask() {
    inRollbackTransaction(() -> {
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
    database.inTransaction(() -> {
      TopicApplication app = appDao.findById((long) 1);
      List<Task> tasks = taskDao.findByTopicApplication(app);
      assertEquals(3, tasks.size());
    });
  }

  @Test
  public void fetchTask() {
    database.inTransaction(() -> {
      Task task = taskDao.findById((long) 4);
      assertEquals((long) 3, (long) task.getApplication().getId());
      assertTrue(task.isCompleted());
    });
  }

  @Test
  public void deleteTask() {
    inRollbackTransaction(() -> {
      Task task = taskDao.findById((long) 1);
      taskDao.delete(task);

      List<Task> tasks = taskDao.findByTopicApplication(task.getApplication());
      assertEquals(2, tasks.size());
      assertFalse(tasks.contains(task));
    });
  }
}
package cz.studenthub.db;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import cz.studenthub.core.Faculty;
import cz.studenthub.core.Topic;
import cz.studenthub.core.TopicApplication;
import cz.studenthub.core.TopicDegree;
import cz.studenthub.core.TopicGrade;
import cz.studenthub.core.User;

public class TopicApplicationDAOTest extends AbstractDAOTest {

  private static FacultyDAO facDAO;
  private static UserDAO userDAO;
  private static TopicDAO topicDAO;
  private static TopicApplicationDAO appDAO;

  @BeforeClass
  public static void setUp() {
    topicDAO = new TopicDAO(database.getSessionFactory());
    facDAO = new FacultyDAO(database.getSessionFactory());
    userDAO = new UserDAO(database.getSessionFactory());
    appDAO = new TopicApplicationDAO(database.getSessionFactory());
  }

  @Test
  public void createTopicApplication() {
    inRollbackTransaction(() -> {
      User user = userDAO.findById((long) 10);
      Topic topic = topicDAO.findById((long) 2);
      Faculty faculty = facDAO.findById((long) 5);

      TopicApplication app = new TopicApplication(topic, "test", TopicGrade.A, TopicDegree.HIGH_SCHOOL, new Date(),
          new Date(), faculty, user, user, user);
      TopicApplication created = appDAO.createOrUpdate(app);

      assertNotNull(created.getId());
      assertEquals(app, created);
      assertEquals(8, appDAO.findAll().size());
    });
  }

  @Test
  public void fetchTopicApplication() {
    TopicApplication app = database.inTransaction(() -> {
      return appDAO.findById((long) 2);
    });

    assertNotNull(app);
    assertEquals(TopicDegree.MASTER, app.getDegree());
    assertEquals(TopicGrade.F, app.getGrade());

  }

  @Test
  public void listAllTopicApplication() {
    List<TopicApplication> apps = database.inTransaction(() -> {
      return appDAO.findAll();
    });

    assertNotNull(apps);
    assertEquals(7, apps.size());
  }

  @Test
  public void listAllTopicApplicationBySupervisor() {
    database.inTransaction(() -> {
      User user = userDAO.findById((long) 2);
      List<TopicApplication> apps = appDAO.findBySupervisor(user);

      assertNotNull(apps);
      assertEquals(2, apps.size());
      for (TopicApplication app : apps) {
        assertEquals(user, app.getAcademicSupervisor());
      }
    });
  }

  @Test
  public void listAllTopicApplicationByStudent() {
    database.inTransaction(() -> {
      User user = userDAO.findById((long) 13);
      List<TopicApplication> apps = appDAO.findByStudent(user);

      assertNotNull(apps);
      assertEquals(3, apps.size());
      for (TopicApplication app : apps) {
        assertEquals(user, app.getStudent());
      }
    });
  }

  @Test
  public void listAllTopicApplicationByFaculty() {
    database.inTransaction(() -> {
      Faculty faculty = facDAO.findById((long) 1);
      List<TopicApplication> apps = appDAO.findByFaculty(faculty);

      assertNotNull(apps);
      assertEquals(2, apps.size());
      for (TopicApplication app : apps) {
        assertEquals(faculty, app.getFaculty());
      }
    });
  }

  @Test
  public void listAllTopicApplicationByTopic() {
    database.inTransaction(() -> {
      Topic topic = topicDAO.findById((long) 2);
      List<TopicApplication> apps = appDAO.findByTopic(topic);

      assertNotNull(apps);
      assertEquals(2, apps.size());
      for (TopicApplication app : apps) {
        assertEquals(topic, app.getTopic());
      }
    });
  }

  @Test
  public void removeUniversity() {
    inRollbackTransaction(() -> {
      TopicApplication app = appDAO.findById((long) 5);
      appDAO.delete(app);
      List<TopicApplication> apps = appDAO.findAll();

      assertEquals(6, apps.size());
      assertFalse(apps.contains(app));
    });
  }

}

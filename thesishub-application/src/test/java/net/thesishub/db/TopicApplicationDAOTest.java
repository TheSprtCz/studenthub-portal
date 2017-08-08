package net.thesishub.db;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;

import io.dropwizard.testing.junit.DAOTestRule;
import net.thesishub.DAOTestSuite;
import net.thesishub.core.Faculty;
import net.thesishub.core.Topic;
import net.thesishub.core.TopicApplication;
import net.thesishub.core.TopicDegree;
import net.thesishub.core.TopicGrade;
import net.thesishub.core.User;
import net.thesishub.db.FacultyDAO;
import net.thesishub.db.TopicApplicationDAO;
import net.thesishub.db.TopicDAO;
import net.thesishub.db.TopicDegreeDAO;
import net.thesishub.db.UserDAO;

public class TopicApplicationDAOTest {

  public static final int COUNT = 7;
  private static final DAOTestRule DATABASE = DAOTestSuite.database;
  private static FacultyDAO facDAO;
  private static UserDAO userDAO;
  private static TopicDAO topicDAO;
  private static TopicApplicationDAO appDAO;
  private static TopicDegreeDAO tdDAO;

  @BeforeClass
  public static void setUp() {
    topicDAO = new TopicDAO(DATABASE.getSessionFactory());
    facDAO = new FacultyDAO(DATABASE.getSessionFactory());
    userDAO = new UserDAO(DATABASE.getSessionFactory());
    appDAO = new TopicApplicationDAO(DATABASE.getSessionFactory());
    tdDAO = new TopicDegreeDAO(DATABASE.getSessionFactory());
  }

  @Test
  public void createTopicApplication() {
    DAOTestSuite.inRollbackTransaction(() -> {
      User user = userDAO.findById((long) 10);
      Topic topic = topicDAO.findById((long) 2);
      Faculty faculty = facDAO.findById((long) 5);
      TopicDegree highSchool = tdDAO.findByName("HIGH_SCHOOL");

      TopicApplication app = new TopicApplication(topic, "test", TopicGrade.A, highSchool, new Date(),
          new Date(), faculty, user, user, user, null);
      TopicApplication created = appDAO.create(app);

      assertNotNull(created.getId());
      assertEquals(app, created);
      assertEquals(COUNT + 1, appDAO.findAll().size());
    });
  }

  @Test
  public void fetchTopicApplication() {
    TopicApplication app = DATABASE.inTransaction(() -> {
      return appDAO.findById((long) 2);
    });

    assertNotNull(app);
    assertEquals(TopicGrade.F, app.getGrade());
  }

  @Test
  public void listAllTopicApplication() {
    List<TopicApplication> apps = DATABASE.inTransaction(() -> {
      return appDAO.findAll();
    });

    assertNotNull(apps);
    assertEquals(COUNT, apps.size());
  }

  @Test
  public void listAllTopicApplicationBySupervisor() {
    DATABASE.inTransaction(() -> {
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
    DATABASE.inTransaction(() -> {
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
    DATABASE.inTransaction(() -> {
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
    DATABASE.inTransaction(() -> {
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
  public void listAllTopicApplicationByTopics() {
    DATABASE.inTransaction(() -> {
      List<Topic> topics = Lists.newArrayList(topicDAO.findById((long) 2), topicDAO.findById((long) 1));
      List<TopicApplication> apps = appDAO.findByTopics(topics);

      assertNotNull(apps);
      assertEquals(5, apps.size());
      for (TopicApplication app : apps) {
        assertTrue(topics.contains(app.getTopic()));
      }
    });
  }

  @Test
  public void removeTopicApplication() {
    DAOTestSuite.inRollbackTransaction(() -> {
      TopicApplication app = appDAO.findById((long) 5);
      appDAO.delete(app);
      List<TopicApplication> apps = appDAO.findAll();

      assertEquals(COUNT - 1, apps.size());
      assertFalse(apps.contains(app));
    });
  }

}

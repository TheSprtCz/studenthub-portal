package cz.studenthub.db;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.studenthub.DAOTestSuite;
import cz.studenthub.core.Company;
import cz.studenthub.core.Topic;
import cz.studenthub.core.TopicDegree;
import cz.studenthub.core.User;
import io.dropwizard.testing.junit.DAOTestRule;

public class TopicDAOTest {

  private static final DAOTestRule DATABASE = DAOTestSuite.database;
  private static CompanyDAO companyDAO;
  private static UserDAO userDAO;
  private static TopicDAO topicDAO;
  private static TopicDegreeDAO tdDAO;

  @BeforeClass
  public static void setUp() {
    topicDAO = new TopicDAO(DATABASE.getSessionFactory());
    companyDAO = new CompanyDAO(DATABASE.getSessionFactory());
    userDAO = new UserDAO(DATABASE.getSessionFactory());
    tdDAO = new TopicDegreeDAO(DATABASE.getSessionFactory());
  }

  /*
   * Had to combine create and delete tests because, rollback transaction isn't
   * able to remove associated tables as well
   */
  @Test
  public void createAndDeleteTopic() {
    DAOTestSuite.inRollbackTransaction(() -> {
      User user = userDAO.findById((long) 10);
      HashSet<TopicDegree> degrees = new HashSet<TopicDegree>();
      degrees.add(tdDAO.findByName("MASTER"));

      Topic topic = new Topic("Topic", null, "short", "description", null, user, null, null, degrees);
      Topic created = topicDAO.create(topic);
      assertNotNull(created.getId());
      assertEquals(topic, created);
      assertEquals(6, topicDAO.findAll().size());
      topicDAO.delete(created);
      assertEquals(5, topicDAO.findAll().size());
    });
  }

  @Test
  public void fetchTopic() {
    Topic topic = DATABASE.inTransaction(() -> {
      return topicDAO.findById((long) 2);
    });

    assertNotNull(topic);
    assertEquals("Dropwizard", topic.getTitle());
    assertEquals("RESTové endpointy", topic.getSecondaryDescription());
  }

  @Test
  public void listAllTopics() {
    List<Topic> topics = DATABASE.inTransaction(() -> {
      return topicDAO.findAll();
    });
    assertNotNull(topics);
    assertEquals(5, topics.size());
  }

  @Test
  public void listAllTopicsByCreator() {
    DATABASE.inTransaction(() -> {
      User user = userDAO.findById((long) 9);
      List<Topic> topics = topicDAO.findByCreator(user);

      assertNotNull(topics);
      assertEquals(2, topics.size());
      for (Topic topic : topics) {
        assertEquals(user, topic.getCreator());
      }
    });
  }

  @Test
  public void listAllTopicsBySupervisor() {
    DATABASE.inTransaction(() -> {
      User user = userDAO.findById((long) 2);
      List<Topic> topics = topicDAO.findBySupervisor(user);

      assertNotNull(topics);
      assertEquals(2, topics.size());
      for (Topic topic : topics) {
        assertTrue(topic.getAcademicSupervisors().contains(user));
      }
    });
  }

  @Test
  public void listAllTopicsByTag() {
    List<Topic> topics = DATABASE.inTransaction(() -> {
      return topicDAO.findByTag("Java");
    });
    assertNotNull(topics);
    assertEquals(2, topics.size());
    for (Topic topic : topics) {
      assertTrue(topic.getTags().contains("Java"));
    }
  }

  @Test
  public void listAllTopicsByCompany() {
    DATABASE.inTransaction(() -> {
      Company company = companyDAO.findById((long) 5);
      List<Topic> topics = topicDAO.findByCompany(company);

      assertNotNull(topics);
      assertEquals(2, topics.size());
      for (Topic topic : topics) {
        assertEquals(company, topic.getCreator().getCompany());
      }
    });
  }

  @Test
  public void search() {
    List<Topic> topics = DATABASE.inTransaction(() -> {
      return topicDAO.search("Java");
    });
    assertNotNull(topics);
    assertEquals(3, topics.size());
  }

}

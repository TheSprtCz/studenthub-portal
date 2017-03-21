package cz.studenthub.db;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import cz.studenthub.core.Company;
import cz.studenthub.core.Topic;
import cz.studenthub.core.TopicDegree;
import cz.studenthub.core.User;

public class TopicDAOTest extends AbstractDAOTest {

  private static CompanyDAO companyDAO;
  private static UserDAO userDAO;
  private static TopicDAO topicDAO;

  @BeforeClass
  public static void setUp() {
    topicDAO = new TopicDAO(database.getSessionFactory());
    companyDAO = new CompanyDAO(database.getSessionFactory());
    userDAO = new UserDAO(database.getSessionFactory());
  }

  /*
   * Had to combine create and delete tests because, rollback transaction isn't
   * able to remove associated tables as well
   */
  @Test
  public void createAndDeleteTopic() {
    inRollbackTransaction(() -> {
      User user = userDAO.findById((long) 10);
      HashSet<TopicDegree> degrees = new HashSet<TopicDegree>();
      degrees.add(TopicDegree.MASTER);

      Topic topic = new Topic("Topic", "short", "description", user, null, null, degrees);
      Topic created = topicDAO.createOrUpdate(topic);
      assertNotNull(created.getId());
      assertEquals(topic, created);
      assertEquals(5, topicDAO.findAll().size());
      topicDAO.delete(created);
      assertEquals(4, topicDAO.findAll().size());
    });
  }

  @Test
  public void fetchTopic() {
    Topic topic = database.inTransaction(() -> {
      return topicDAO.findById((long) 2);
    });

    assertNotNull(topic);
    assertEquals("Dropwizard", topic.getTitle());

  }

  @Test
  public void listAllTopics() {
    List<Topic> topics = database.inTransaction(() -> {
      return topicDAO.findAll();
    });
    assertNotNull(topics);
    assertEquals(4, topics.size());
  }

  @Test
  public void listAllTopicsByCreator() {
    database.inTransaction(() -> {
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
    database.inTransaction(() -> {
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
    List<Topic> topics = database.inTransaction(() -> {
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
    database.inTransaction(() -> {
      Company company = companyDAO.findById((long) 5);
      List<Topic> topics = topicDAO.findByCompany(company);

      assertNotNull(topics);
      assertEquals(2, topics.size());
      for (Topic topic : topics) {
        assertEquals(company, topic.getCreator().getCompany());
      }
    });
  }

}

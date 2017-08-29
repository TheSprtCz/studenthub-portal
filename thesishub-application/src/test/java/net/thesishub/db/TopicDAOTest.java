package net.thesishub.db;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;

import io.dropwizard.testing.junit.DAOTestRule;
import jersey.repackaged.com.google.common.collect.Sets;
import net.thesishub.DAOTestSuite;
import net.thesishub.core.Company;
import net.thesishub.core.Topic;
import net.thesishub.core.TopicDegree;
import net.thesishub.core.User;
import net.thesishub.db.CompanyDAO;
import net.thesishub.db.TopicDAO;
import net.thesishub.db.TopicDegreeDAO;
import net.thesishub.db.UserDAO;

public class TopicDAOTest {

  public static final int COUNT = 5;
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
      assertEquals(COUNT + 1, topicDAO.findAll().size());
      topicDAO.delete(created);
      assertEquals(COUNT, topicDAO.findAll().size());
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
    assertEquals(COUNT, topics.size());
  }

  @Test
  public void listAllOrdered() {
    List<Topic> topics = DATABASE.inTransaction(() -> {
      return topicDAO.findAllOrdered(3);
    });
    assertNotNull(topics);
    assertEquals(3, topics.size());

    Date previousDate = topics.get(0).getDateCreated();
    for (Topic topic : topics) {
      Date currentDate = topic.getDateCreated();
      assertTrue(currentDate.equals(previousDate) || currentDate.after(previousDate));
      previousDate = currentDate;
    }
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
      return topicDAO.search("Java", new HashSet<Long>(), new HashSet<String>());
    });
    assertNotNull(topics);
    assertEquals(3, topics.size());

    topics = DATABASE.inTransaction(() -> {
      return topicDAO.search("", Sets.newHashSet((long) 1, (long) 3) , Sets.newHashSet("BACHELOR"));
    });
    assertNotNull(topics);
    assertEquals(0, topics.size());

    topics = DATABASE.inTransaction(() -> {
      return topicDAO.search("", Sets.newHashSet((long) 5, (long) 6) , Sets.newHashSet("MASTER"));
    });
    assertNotNull(topics);
    assertEquals(2, topics.size());
  }

  @Test
  public void listAllHighlighted() {
    List<Topic> topics = DATABASE.inTransaction(() -> {
      return topicDAO.findHighlighted();
    });
    assertNotNull(topics);
    assertEquals(3, topics.size());
  }

}

package cz.studenthub.db;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import cz.studenthub.DAOTestSuite;
import cz.studenthub.core.TopicDegree;
import io.dropwizard.testing.junit.DAOTestRule;

public class TopicDegreeDAOTest {

  private static final DAOTestRule DATABASE = DAOTestSuite.database;
  private static TopicDegreeDAO tdDAO;

  @BeforeClass
  public static void setUp() {
    tdDAO = new TopicDegreeDAO(DATABASE.getSessionFactory());
  }

  @Test
  public void createTopicDegree() {
    DAOTestSuite.inRollbackTransaction(() -> {
      TopicDegree degree = new TopicDegree("Super", "Wow");
      TopicDegree created = tdDAO.create(degree);
      List<TopicDegree> degrees = tdDAO.findAll();

      assertEquals(degree, created);
      assertEquals(6, degrees.size());
    });
  }

  @Test
  public void fetchTopicDegree() {
    TopicDegree degree = DATABASE.inTransaction(() -> {
      return tdDAO.findByName("BACHELOR");
    });

    assertNotNull(degree);
    assertEquals("Undergraduate", degree.getDescription());
  }

  @Test
  public void listAllTopicDegrees() {
    List<TopicDegree> degrees = DATABASE.inTransaction(() -> {
      return tdDAO.findAll();
    });
    assertNotNull(degrees);
    assertEquals(5, degrees.size());
  }

  @Test
  public void removeTopicDegree() {
    DAOTestSuite.inRollbackTransaction(() -> {
      TopicDegree degree = tdDAO.findByName("DELETABLE");
      tdDAO.delete(degree);
      List<TopicDegree> degrees = tdDAO.findAll();

      assertNotNull(degree);
      assertEquals(4, degrees.size());
      assertFalse(degrees.contains(degree));
    });
  }
}

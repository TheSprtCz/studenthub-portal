package cz.studenthub.db;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.studenthub.DAOTestSuite;
import cz.studenthub.core.Company;
import cz.studenthub.core.Faculty;
import cz.studenthub.core.Project;
import cz.studenthub.core.Topic;
import cz.studenthub.core.User;
import io.dropwizard.testing.junit.DAOTestRule;

public class ProjectDAOTest {

  public static final int COUNT = 2;
  private static final DAOTestRule DATABASE = DAOTestSuite.database;
  private static CompanyDAO companyDAO;
  private static FacultyDAO facultyDAO;
  private static UserDAO userDAO;
  private static TopicDAO topicDAO;
  private static ProjectDAO projectDAO;

  @BeforeClass
  public static void setUp() {
    topicDAO = new TopicDAO(DATABASE.getSessionFactory());
    companyDAO = new CompanyDAO(DATABASE.getSessionFactory());
    userDAO = new UserDAO(DATABASE.getSessionFactory());
    facultyDAO = new FacultyDAO(DATABASE.getSessionFactory());
    projectDAO = new ProjectDAO(DATABASE.getSessionFactory());
  }

  /*
   * Had to combine create and delete tests because, rollback transaction isn't
   * able to remove associated tables as well
   */
  @Test
  public void createAndDeleteProject() {
    DAOTestSuite.inRollbackTransaction(() -> {
      User user = userDAO.findById((long) 10);
      HashSet<User> creators = new HashSet<User>();
      creators.add(user);

      Project project = new Project("Project1", null, creators, null, null, null);
      Project created = projectDAO.create(project);
      assertNotNull(created.getId());
      assertEquals(project, created);
      assertEquals(COUNT + 1, projectDAO.findAll().size());
      projectDAO.delete(created);
      assertEquals(COUNT, projectDAO.findAll().size());
    });
  }

  @Test
  public void fetchProject() {
    Project project = DATABASE.inTransaction(() -> {
      return projectDAO.findById((long) 1);
    });

    assertNotNull(project);
    assertEquals("Web stuff", project.getName());
  }

  @Test
  public void listAllProjects() {
    List<Project> topics = DATABASE.inTransaction(() -> {
      return projectDAO.findAll();
    });
    assertNotNull(topics);
    assertEquals(COUNT, topics.size());
  }

  @Test
  public void listAllProjectsByCreator() {
    DATABASE.inTransaction(() -> {
      User user = userDAO.findById((long) 9);
      List<Project> projects = projectDAO.findByCreator(user);

      assertNotNull(projects);
      assertEquals(1, projects.size());
      for (Project project : projects) {
        assertTrue(projects.contains(project));
      }
    });
  }

  @Test
  public void listAllProjectsByFaculty() {
    DATABASE.inTransaction(() -> {
      Faculty faculty = facultyDAO.findById((long) 6);
      List<Project> projects = projectDAO.findByFaculty(faculty);

      assertNotNull(projects);
      assertEquals(2, projects.size());
      for (Project project : projects) {
        assertTrue(project.getFaculties().contains(faculty));
      }
    });
  }

  @Test
  public void listAllProjectsByTopic() {
    DATABASE.inTransaction(() -> {
      Topic topic = topicDAO.findById((long) 1);
      List<Project> projects = projectDAO.findByTopic(topic);
      assertNotNull(projects);
      assertEquals(1, projects.size());
      for (Project project : projects) {
        assertTrue(project.getTopics().contains(topic));
      }
    });
  }

  @Test
  public void listAllTopicsByCompany() {
    DATABASE.inTransaction(() -> {
      Company company = companyDAO.findById((long) 5);
      List<Project> projects = projectDAO.findByCompany(company);

      assertNotNull(projects);
      assertEquals(1, projects.size());
      for (Project project : projects) {
        assertTrue(project.getCompanies().contains(company));
      }
    });
  }

}

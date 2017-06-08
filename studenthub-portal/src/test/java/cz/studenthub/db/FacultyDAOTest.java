package cz.studenthub.db;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import cz.studenthub.DAOTestSuite;
import cz.studenthub.core.Faculty;
import cz.studenthub.core.University;
import io.dropwizard.testing.junit.DAOTestRule;

public class FacultyDAOTest {

  private static final DAOTestRule DATABASE = DAOTestSuite.database;
  private static FacultyDAO facDAO;
  private static UniversityDAO uniDAO;

  @BeforeClass
  public static void setUp() {
    facDAO = new FacultyDAO(DATABASE.getSessionFactory());
    uniDAO = new UniversityDAO(DATABASE.getSessionFactory());
  }

  @Test
  public void createFaculty() {
    DAOTestSuite.inRollbackTransaction(() -> {
      University uni = uniDAO.findById((long) 2);
      Faculty faculty = new Faculty("New", uni);

      Faculty created = facDAO.create(faculty);
      assertNotNull(created.getId());
      assertEquals(faculty, created);
      assertEquals(14, facDAO.findAll().size());
    });
  }

  @Test
  public void fetchFaculty() {
    Faculty faculty = DATABASE.inTransaction(() -> {
      return facDAO.findById((long) 6);
    });

    assertNotNull(faculty);
    assertEquals("Brno University of Technology", faculty.getUniversity().getName());
    assertEquals("Faculty of Electrical Engineering and Communication", faculty.getName());

  }

  @Test
  public void listAllFaculties() {
    List<Faculty> faculties = DATABASE.inTransaction(() -> {
      return facDAO.findAll();
    });
    assertNotNull(faculties);
    assertEquals(faculties.size(), 13);
  }

  @Test
  public void listAllFacultiesByUniversity() {
    List<Faculty> faculties = DATABASE.inTransaction(() -> {
      University uni = uniDAO.findById((long) 2);
      return facDAO.findAllByUniversity(uni);
    });
    assertNotNull(faculties);
    assertEquals(3, faculties.size());
  }

  @Test
  public void removeUniversity() {
    DAOTestSuite.inRollbackTransaction(() -> {
      Faculty faculty = facDAO.findById((long) 12);
      facDAO.delete(faculty);
      List<Faculty> faculties = facDAO.findAll();
      assertNotNull(faculty);
      assertEquals(12, faculties.size());
      assertFalse(faculties.contains(faculty));
    });
  }
}

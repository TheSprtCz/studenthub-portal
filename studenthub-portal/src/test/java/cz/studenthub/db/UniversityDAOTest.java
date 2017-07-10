package cz.studenthub.db;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import cz.studenthub.DAOTestSuite;
import cz.studenthub.core.Country;
import cz.studenthub.core.University;
import io.dropwizard.testing.junit.DAOTestRule;

public class UniversityDAOTest {

  private static final DAOTestRule DATABASE = DAOTestSuite.database;
  private static UniversityDAO uniDAO;
  private static CountryDAO countryDAO;

  @BeforeClass
  public static void setUp() {
    uniDAO = new UniversityDAO(DATABASE.getSessionFactory());
    countryDAO = new CountryDAO(DATABASE.getSessionFactory());
  }

  @Test
  public void createUniversity() {
    DAOTestSuite.inRollbackTransaction(() -> {
      Country country = countryDAO.findByTag("CZ");
      University uni = new University("BUT", "http://www.nothing.com", "Brno", country, "http://nothing.com/img.jpg");
      University created = uniDAO.create(uni);
      List<University> universities = uniDAO.findAll();
      assertNotNull(created.getId());
      assertEquals(uni, created);
      assertEquals(6, universities.size());
    });
  }

  @Test
  public void fetchUniversity() {
    University university = DATABASE.inTransaction(() -> {
      return uniDAO.findById((long) 1);
    });

    assertNotNull(university);
    assertEquals("Masaryk University", university.getName());
    assertEquals("Brno", university.getCity());

  }

  @Test
  public void listAllUniversities() {
    List<University> universities = DATABASE.inTransaction(() -> {
      return uniDAO.findAll();
    });
    assertNotNull(universities);
    assertEquals(universities.size(), 5);
  }

  @Test
  public void removeUniversity() {
    DAOTestSuite.inRollbackTransaction(() -> {
      University university = uniDAO.findById((long) 5);
      uniDAO.delete(university);
      List<University> universities = uniDAO.findAll();

      assertEquals(4, universities.size());
      assertFalse(universities.contains(university));
    });
  }
}

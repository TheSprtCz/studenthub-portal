package net.thesishub.db;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import io.dropwizard.testing.junit.DAOTestRule;
import net.thesishub.DAOTestSuite;
import net.thesishub.core.Country;
import net.thesishub.core.University;
import net.thesishub.db.CountryDAO;
import net.thesishub.db.UniversityDAO;

public class UniversityDAOTest {

  public static final int COUNT = 5;
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
      assertEquals(COUNT + 1, universities.size());
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
    assertEquals(COUNT, universities.size());
  }

  @Test
  public void removeUniversity() {
    DAOTestSuite.inRollbackTransaction(() -> {
      University university = uniDAO.findById((long) 5);
      uniDAO.delete(university);
      List<University> universities = uniDAO.findAll();

      assertEquals(COUNT - 1, universities.size());
      assertFalse(universities.contains(university));
    });
  }
  
  @Test
  public void searchUniversity() {
    List<University> universities = DATABASE.inTransaction(() -> {
      return uniDAO.search("masaryk");
    });

    assertNotNull(universities);
    assertEquals(1, universities.size());
  }
}

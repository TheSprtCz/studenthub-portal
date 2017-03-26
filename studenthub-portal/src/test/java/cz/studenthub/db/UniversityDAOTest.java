package cz.studenthub.db;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import cz.studenthub.core.Country;
import cz.studenthub.core.University;

public class UniversityDAOTest extends AbstractDAOTest {

  private static UniversityDAO uniDAO;

  @BeforeClass
  public static void setUp() {
    uniDAO = new UniversityDAO(database.getSessionFactory());
  }

  @Test
  public void createUniversity() {
    University uni = new University("BUT", "www.nothing.com", "Brno", Country.CZ, "/img.jpg");
    inRollbackTransaction(() -> {
      University created = uniDAO.createOrUpdate(uni);
      List<University> universities = uniDAO.findAll();
      assertNotNull(created.getId());
      assertEquals(uni, created);
      assertEquals(6, universities.size());
    });
  }

  @Test
  public void fetchUniversity() {
    University university = database.inTransaction(() -> {
      return uniDAO.findById((long) 1);
    });

    assertNotNull(university);
    assertEquals("Masaryk University", university.getName());
    assertEquals("Brno", university.getCity());

  }

  @Test
  public void listAllUniversities() {
    List<University> universities = database.inTransaction(() -> {
      return uniDAO.findAll();
    });
    assertNotNull(universities);
    assertEquals(universities.size(), 5);
  }

  @Test
  public void removeUniversity() {
    inRollbackTransaction(() -> {
      University university = uniDAO.findById((long) 5);
      uniDAO.delete(university);
      List<University> universities = uniDAO.findAll();

      assertEquals(4, universities.size());
      assertFalse(universities.contains(university));
    });
  }
}

package cz.studenthub.db;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import cz.studenthub.DAOTestSuite;
import cz.studenthub.core.Country;
import io.dropwizard.testing.junit.DAOTestRule;

public class CountryDAOTest {

  public static final int COUNT = 3;
  private static final DAOTestRule DATABASE = DAOTestSuite.database;
  private static CountryDAO cpDAO;

  @BeforeClass
  public static void setUp() {
    cpDAO = new CountryDAO(DATABASE.getSessionFactory());
  }

  @Test
  public void createCountry() {
    DAOTestSuite.inRollbackTransaction(() -> {
      Country country = new Country("PL", "Poland");
      Country created = cpDAO.create(country);
      List<Country> countries = cpDAO.findAll();

      assertEquals(country, created);
      assertEquals(COUNT + 1, countries.size());
    });
  }

  @Test
  public void fetchCountry() {
    Country country = DATABASE.inTransaction(() -> {
      return cpDAO.findByTag("SK");
    });

    assertNotNull(country);
    assertEquals("Slovakia" , country.getName());
  }

  @Test
  public void listAllCountries() {
    List<Country> countries = DATABASE.inTransaction(() -> {
      return cpDAO.findAll();
    });
    assertNotNull(countries);
    assertEquals(COUNT, countries.size());
  }

  @Test
  public void removeCountry() {
    DAOTestSuite.inRollbackTransaction(() -> {
      Country country = cpDAO.findByTag("HU");
      cpDAO.delete(country);
      List<Country> countries = cpDAO.findAll();
      assertNotNull(country);
      assertEquals(COUNT - 1, countries.size());
      assertFalse(countries.contains(country));
    });
  }
}

package cz.studenthub.db;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import cz.studenthub.DAOTestSuite;
import cz.studenthub.core.Company;
import cz.studenthub.core.CompanyPlan;
import cz.studenthub.core.CompanySize;
import cz.studenthub.core.Country;
import io.dropwizard.testing.junit.DAOTestRule;

public class CompanyDAOTest {

  private static final DAOTestRule DATABASE = DAOTestSuite.database;
  private static CompanyDAO companyDAO;
  private static CompanyPlanDAO cpDAO;

  @BeforeClass
  public static void setUp() {
    companyDAO = new CompanyDAO(DATABASE.getSessionFactory());
    cpDAO = new CompanyPlanDAO(DATABASE.getSessionFactory());
  }

  @Test
  public void createCompany() {
    DAOTestSuite.inRollbackTransaction(() -> {
      CompanyPlan cPlan = cpDAO.findByName("TIER_2");
      Company company = new Company("New", "www.nothing.eu", "Liberec", Country.CZ, "www.nothing.eu/logo.png",
          CompanySize.SMALL, cPlan);
      Company created = companyDAO.create(company);
      List<Company> companies = companyDAO.findAll();
      assertNotNull(created.getId());
      assertEquals(company, created);
      assertEquals(9, companies.size());
    });
  }

  @Test
  public void fetchCompany() {
    Company company = DATABASE.inTransaction(() -> {
      return companyDAO.findById((long) 1);
    });

    assertNotNull(company);
    assertEquals("Company One", company.getName());
    assertEquals("Brno", company.getCity());

  }

  @Test
  public void listAllCompanies() {
    List<Company> companies = DATABASE.inTransaction(() -> {
      return companyDAO.findAll();
    });
    assertNotNull(companies);
    assertEquals(8, companies.size());
  }

  @Test
  public void removeCompany() {
    DAOTestSuite.inRollbackTransaction(() -> {
      Company company = companyDAO.findById((long) 3);
      companyDAO.delete(company);
      List<Company> companies = companyDAO.findAll();
      assertNotNull(company);
      assertEquals(7, companies.size());
      assertFalse(companies.contains(company));
    });
  }
}

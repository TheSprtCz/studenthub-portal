package net.thesishub.db;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import io.dropwizard.testing.junit.DAOTestRule;
import net.thesishub.DAOTestSuite;
import net.thesishub.core.CompanyPlan;
import net.thesishub.db.CompanyPlanDAO;

public class CompanyPlanDAOTest {

  public static final int COUNT = 4;
  private static final DAOTestRule DATABASE = DAOTestSuite.database;
  private static CompanyPlanDAO cpDAO;

  @BeforeClass
  public static void setUp() {
    cpDAO = new CompanyPlanDAO(DATABASE.getSessionFactory());
  }

  @Test
  public void createCompanyPlan() {
    DAOTestSuite.inRollbackTransaction(() -> {
      CompanyPlan cPlan = new CompanyPlan("TIER_5", 50);
      CompanyPlan created = cpDAO.create(cPlan);
      List<CompanyPlan> cPlans = cpDAO.findAll();

      assertEquals(cPlan, created);
      assertEquals(COUNT + 1, cPlans.size());
    });
  }

  @Test
  public void fetchCompanyPlan() {
    CompanyPlan cPlan = DATABASE.inTransaction(() -> {
      return cpDAO.findByName("TIER_2");
    });

    assertNotNull(cPlan);
    assertEquals(5, cPlan.getMaxTopics());
  }

  @Test
  public void listAllCompanyPlans() {
    List<CompanyPlan> cPlans = DATABASE.inTransaction(() -> {
      return cpDAO.findAll();
    });
    assertNotNull(cPlans);
    assertEquals(COUNT, cPlans.size());
  }

  @Test
  public void removeCompanyPlan() {
    DAOTestSuite.inRollbackTransaction(() -> {
      CompanyPlan cPlan = cpDAO.findByName("TIER_4");
      cpDAO.delete(cPlan);
      List<CompanyPlan> cPlans = cpDAO.findAll();
      assertNotNull(cPlan);
      assertEquals(COUNT - 1, cPlans.size());
      assertFalse(cPlans.contains(cPlan));
    });
  }
}

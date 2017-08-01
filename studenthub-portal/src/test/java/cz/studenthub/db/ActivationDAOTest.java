package cz.studenthub.db;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import cz.studenthub.DAOTestSuite;
import cz.studenthub.core.Activation;
import cz.studenthub.core.ActivationType;
import cz.studenthub.core.User;
import io.dropwizard.testing.junit.DAOTestRule;

public class ActivationDAOTest {

  public static final int COUNT = 3;
  private static final DAOTestRule DATABASE = DAOTestSuite.database;
  private static ActivationDAO actDAO;
  private static UserDAO userDAO;

  @BeforeClass
  public static void setUp() {
    actDAO = new ActivationDAO(DATABASE.getSessionFactory());
    userDAO = new UserDAO(DATABASE.getSessionFactory());
  }

  @Test
  public void createActivation() {
    DAOTestSuite.inRollbackTransaction(() -> {
      User user = userDAO.findById((long) 1);
      Activation act = new Activation(user, ActivationType.REGISTER);

      Activation created = actDAO.create(act);

      List<Activation> activations = actDAO.findAll();
      assertNotNull(created.getId());
      assertEquals(act, created);
      assertEquals(COUNT + 1, activations.size());
    });
  }

  @Test
  public void fetchActivation() {
    Activation act = DATABASE.inTransaction(() -> {
      return actDAO.findById((long) 1);
    });

    assertNotNull(act);
    assertEquals("rep3", act.getActivationCode());
    assertEquals("rep3", act.getUser().getUsername());
  }

  @Test
  public void fetchActivationByUser() {
    Activation act = DATABASE.inTransaction(() -> {
      User user = userDAO.findById((long) 9);
      return actDAO.findByUser(user);
    });

    assertNotNull(act);
    assertEquals("leader2", act.getActivationCode());
    assertEquals(ActivationType.PASSWORD_RESET, act.getType());
  }

  @Test
  public void fetchActivationByUserAndType() {
    Activation act = DATABASE.inTransaction(() -> {
      User user = userDAO.findById((long) 15);
      return actDAO.findByUserAndType(user, ActivationType.REGISTER);
    });

    assertNotNull(act);
    assertEquals("student4", act.getActivationCode());
    assertEquals(ActivationType.REGISTER, act.getType());
  }

  @Test
  public void listAllActivations() {
    List<Activation> activations = DATABASE.inTransaction(() -> {
      return actDAO.findAll();
    });
    assertNotNull(activations);
    assertEquals(COUNT, activations.size());
  }

  @Test
  public void removeActivation() {
    DAOTestSuite.inRollbackTransaction(() -> {
      Activation activation = actDAO.findById((long) 2);

      actDAO.delete(activation);
      List<Activation> activations = actDAO.findAll();
      assertNotNull(activation);
      assertEquals(COUNT - 1, activations.size());
      assertFalse(activations.contains(activation));
    });
  }
}

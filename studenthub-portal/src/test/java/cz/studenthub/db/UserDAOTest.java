package cz.studenthub.db;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.studenthub.DAOTestSuite;
import cz.studenthub.core.Company;
import cz.studenthub.core.Faculty;
import cz.studenthub.core.User;
import cz.studenthub.core.UserRole;
import io.dropwizard.testing.junit.DAOTestRule;

public class UserDAOTest {

  private static final DAOTestRule DATABASE = DAOTestSuite.database;
  private static FacultyDAO facDAO;
  private static CompanyDAO companyDAO;
  private static UserDAO userDAO;

  @BeforeClass
  public static void setUp() {
    facDAO = new FacultyDAO(DATABASE.getSessionFactory());
    companyDAO = new CompanyDAO(DATABASE.getSessionFactory());
    userDAO = new UserDAO(DATABASE.getSessionFactory());
  }

  /*
   * Had to combine create and delete tests because, rollback transaction wasn't
   * able to remove roles as well and kept throwing errors
   */
  @Test
  public void createAndDeleteUser() {
    DATABASE.inTransaction(() -> {
      Faculty faculty = facDAO.findById((long) 12);
      HashSet<UserRole> roles = new HashSet<UserRole>();
      roles.add(UserRole.ADMIN);

      User user = new User("test", "test", "email@mail.me", "Test Tester", "000 222 555", faculty, null, roles, null);
      User created = userDAO.create(user);
      assertNotNull(created.getId());
      assertEquals(user, created);
      assertEquals(21, userDAO.findAll().size());
      userDAO.delete(created);
      assertEquals(20, userDAO.findAll().size());
    });
  }

  @Test
  public void fetchUser() {
    User user = DATABASE.inTransaction(() -> {
      return userDAO.findById((long) 6);
    });

    assertNotNull(user);
    assertEquals("supervisor5", user.getUsername());

  }

  @Test
  public void listAllUsers() {
    List<User> users = DATABASE.inTransaction(() -> {
      return userDAO.findAll();
    });
    assertNotNull(users);
    assertEquals(20, users.size());
  }

  @Test
  public void listAllUsersByRole() {
    List<User> users = DATABASE.inTransaction(() -> {
      return userDAO.findByRole(UserRole.STUDENT);
    });
    assertNotNull(users);
    assertEquals(5, users.size());
    users = DATABASE.inTransaction(() -> {
      return userDAO.findByRole(UserRole.AC_SUPERVISOR);
    });
    assertNotNull(users);
    assertEquals(7, users.size());
  }

  @Test
  public void listAllUsersByCompany() {
    List<User> users = DATABASE.inTransaction(() -> {
      Company company = companyDAO.findById((long) 1);

      return userDAO.findByCompany(company);
    });
    assertNotNull(users);
    assertEquals(3, users.size());
  }

  @Test
  public void listAllUsersByTag() {
    List<User> users = DATABASE.inTransaction(() -> {
      return userDAO.findByTag("Java");
    });
    assertNotNull(users);
    assertEquals(4, users.size());
  }

  @Test
  public void listAllUsersByRoleAndFaculty() {
    DATABASE.inTransaction(() -> {
      Faculty faculty = facDAO.findById((long) 1);

      List<User> students = userDAO.findByRoleAndFaculty(UserRole.STUDENT, faculty);
      List<User> supervisors = userDAO.findByRoleAndFaculty(UserRole.AC_SUPERVISOR, faculty);

      assertNotNull(students);
      assertEquals(3, students.size());
      assertNotNull(supervisors);
      assertEquals(2, supervisors.size());
    });
  }

  @Test
  public void findUserByUsername() {
    User user = DATABASE.inTransaction(() -> {
      return userDAO.findByUsername("supervisor1");
    });

    assertNotNull(user);
    assertEquals("258 457 987", user.getPhone());
  }

  @Test
  public void findUserByEmail() {
    User user = DATABASE.inTransaction(() -> {
      return userDAO.findByEmail("admin@example.com");
    });

    assertNotNull(user);
    assertEquals("123 456 789", user.getPhone());
  }

}

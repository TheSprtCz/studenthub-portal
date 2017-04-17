package cz.studenthub.db;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import cz.studenthub.core.Company;
import cz.studenthub.core.Faculty;
import cz.studenthub.core.User;
import cz.studenthub.core.UserRole;

public class UserDAOTest extends AbstractDAOTest {

  private static FacultyDAO facDAO;
  private static CompanyDAO companyDAO;
  private static UserDAO userDAO;

  @BeforeClass
  public static void setUp() {
    facDAO = new FacultyDAO(database.getSessionFactory());
    companyDAO = new CompanyDAO(database.getSessionFactory());
    userDAO = new UserDAO(database.getSessionFactory());
  }

  /*
   * Had to combine create and delete tests because, rollback transaction wasn't
   * able to remove roles as well and kept throwing errors
   */
  @Test
  public void createAndDeleteUser() {
    database.inTransaction(() -> {
      Faculty faculty = facDAO.findById((long) 12);
      HashSet<UserRole> roles = new HashSet<UserRole>();
      roles.add(UserRole.ADMIN);

      User user = new User("test", "test", "email@mail.me", "Test Tester", "000 222 555", faculty, null, roles, null);
      User created = userDAO.create(user);
      assertNotNull(created.getId());
      assertEquals(user, created);
      assertEquals(20, userDAO.findAll().size());
      userDAO.delete(created);
      assertEquals(19, userDAO.findAll().size());
    });
  }

  @Test
  public void fetchUser() {
    User user = database.inTransaction(() -> {
      return userDAO.findById((long) 6);
    });

    assertNotNull(user);
    assertEquals("supervisor5", user.getUsername());

  }

  @Test
  public void listAllUsers() {
    List<User> users = database.inTransaction(() -> {
      return userDAO.findAll();
    });
    assertNotNull(users);
    assertEquals(19, users.size());
  }

  @Test
  public void listAllUsersByRole() {
    List<User> users = database.inTransaction(() -> {
      return userDAO.findByRole(UserRole.STUDENT);
    });
    assertNotNull(users);
    assertEquals(5, users.size());
    users = database.inTransaction(() -> {
      return userDAO.findByRole(UserRole.AC_SUPERVISOR);
    });
    assertNotNull(users);
    assertEquals(7, users.size());
  }

  @Test
  public void listAllUsersByCompany() {
    List<User> users = database.inTransaction(() -> {
      Company company = companyDAO.findById((long) 1);

      return userDAO.findByCompany(company);
    });
    assertNotNull(users);
    assertEquals(2, users.size());
  }

  @Test
  public void listAllUsersByTag() {
    List<User> users = database.inTransaction(() -> {
      return userDAO.findByTag("Java");
    });
    assertNotNull(users);
    assertEquals(4, users.size());
  }

  @Test
  public void listAllUsersByRoleAndFaculty() {
    database.inTransaction(() -> {
      Faculty faculty = facDAO.findById((long) 1);

      List<User> students = userDAO.findByRoleAndFaculty(UserRole.STUDENT, faculty);
      List<User> supervisors = userDAO.findByRoleAndFaculty(UserRole.AC_SUPERVISOR, faculty);

      assertNotNull(students);
      assertEquals(2, students.size());
      assertNotNull(supervisors);
      assertEquals(1, supervisors.size());
    });
  }

}

package net.thesishub.db;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import net.thesishub.DAOTestSuite;
import net.thesishub.core.Notification;
import net.thesishub.core.NotificationType;
import net.thesishub.core.User;
import net.thesishub.db.NotificationDAO;
import net.thesishub.db.UserDAO;
import io.dropwizard.testing.junit.DAOTestRule;

public class NotificationDAOTest {

  public static final int COUNT = 2;
  private final static DAOTestRule DATABASE = DAOTestSuite.database;
  private static NotificationDAO notifDAO;
  private static UserDAO userDAO;

  @BeforeClass
  public static void setUp() {
    notifDAO = new NotificationDAO(DATABASE.getSessionFactory());
    userDAO = new UserDAO(DATABASE.getSessionFactory());
  }

  @Test
  public void createNotification() {
    DAOTestSuite.inRollbackTransaction(() -> {
      User target = userDAO.findById((long) 13);
      Notification notification = new Notification(target, target, null, NotificationType.APPLICATION_CREATED);
      Notification created = notifDAO.create(notification);
      List<Notification> notifications = notifDAO.findByUser(target);

      assertEquals(notification, created);
      assertEquals(COUNT + 1, notifications.size());
    });
  }

  @Test
  public void fetchNotification() {
    Notification notification = DATABASE.inTransaction(() -> {
      return notifDAO.findById((long) 2);
    });

    assertNotNull(notification);
    assertEquals(NotificationType.APPLICATION_GRADED, notification.getType());
  }

  @Test
  public void removeNotification() {
    DAOTestSuite.inRollbackTransaction(() -> {
      User target = userDAO.findById((long) 13);
      Notification notification = notifDAO.findById((long) 2);
      notifDAO.delete(notification);

      List<Notification> notifications = notifDAO.findByUser(target);
      assertNotNull(notification);
      assertEquals(COUNT - 1, notifications.size());
      assertFalse(notifications.contains(notification));
    });
  }

  @Test
  public void listAllNotificationsByUser() {
    DATABASE.inTransaction(() -> {
      User user = userDAO.findById((long) 13);
      List<Notification> notifications = notifDAO.findByUser(user);

      assertNotNull(notifications);
      assertEquals(COUNT, notifications.size());
    });
  }
}

package net.thesishub.integration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.dropwizard.testing.DropwizardTestSupport;
import net.thesishub.IntegrationTestSuite;
import net.thesishub.ThesisHubConfiguration;
import net.thesishub.db.NotificationDAOTest;

public class NotificationResourceTest {
  private DropwizardTestSupport<ThesisHubConfiguration> dropwizard;

  private Client client;

  @BeforeClass
  public void setup() {
      dropwizard = IntegrationTestSuite.DROPWIZARD;
      client = IntegrationTestSuite.BUILDER.build("NotificationTest");
  }

  @Test(dependsOnGroups = "login")
  public void readNotification() {
    Response response = IntegrationTestSuite.oneTimeAuthorizedRequest(client.target(String.format("http://localhost:%d/api/notifications/5/read", dropwizard.getLocalPort()))
        .request(), client, "student2@example.com", "test").post(null);

    assertNotNull(response);
    assertEquals(response.getStatus(), 200);
  }

  @Test(dependsOnGroups = "listNotifications")
  public void deleteNotification() {
    Response response = IntegrationTestSuite.oneTimeAuthorizedRequest(client.target(String.format("http://localhost:%d/api/notifications/2", dropwizard.getLocalPort()))
        .request(), client, "student2@example.com", "test").delete();

    assertNotNull(response);
    assertEquals(response.getStatus(), 204);
    assertEquals(UserResourceTest.fetchNotifications().size(), NotificationDAOTest.COUNT - 1);
  }

}
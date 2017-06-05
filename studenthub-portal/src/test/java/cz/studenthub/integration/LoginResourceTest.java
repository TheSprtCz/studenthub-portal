package cz.studenthub.integration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import cz.studenthub.IntegrationTestSuite;
import cz.studenthub.StudentHubConfiguration;
import io.dropwizard.testing.DropwizardTestSupport;

public class LoginResourceTest {
  public static DropwizardTestSupport<StudentHubConfiguration> DROPWIZARD;

  private static Client client;

  @BeforeClass
  public void setup() {
      DROPWIZARD = IntegrationTestSuite.DROPWIZARD;
      client = IntegrationTestSuite.BUILDER.build("LoginTest");
  }

  @Test(groups = "login", dependsOnGroups = "migrate")
  public void loginTest() {
    Response response = IntegrationTestSuite.authorizationRequest(client, IntegrationTestSuite.USERNAME, IntegrationTestSuite.PASSWORD);

    assertEquals(response.getStatus(), 200);
    assertNotNull(response.getCookies().get("sh-token").getValue());
  }
}

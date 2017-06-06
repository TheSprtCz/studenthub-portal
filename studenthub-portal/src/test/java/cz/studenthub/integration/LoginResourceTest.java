package cz.studenthub.integration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import cz.studenthub.IntegrationTestSuite;

public class LoginResourceTest {
  private Client client;

  @BeforeClass
  public void setup() {
      client = IntegrationTestSuite.BUILDER.build("LoginTest");
  }

  @Test(groups = "login", dependsOnGroups = "migrate")
  public void loginTest() {
    Response response = IntegrationTestSuite.authorizationRequest(client, IntegrationTestSuite.USERNAME, IntegrationTestSuite.PASSWORD);

    assertEquals(response.getStatus(), 200);
    assertNotNull(response.getCookies().get("sh-token").getValue());
  }
}

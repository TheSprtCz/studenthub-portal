package net.thesishub.integration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import net.thesishub.IntegrationTestSuite;
import net.thesishub.resources.LoginResource;

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
    assertNotNull(response.getCookies().get(LoginResource.COOKIE_NAME).getValue());
  }
}

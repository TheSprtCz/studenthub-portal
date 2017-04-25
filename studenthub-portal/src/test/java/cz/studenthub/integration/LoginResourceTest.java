package cz.studenthub.integration;

import static org.junit.Assert.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import org.junit.Test;

import cz.studenthub.IntegrationTestSuite;
import cz.studenthub.StudentHubConfiguration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;

public class LoginResourceTest {
  public static final DropwizardAppRule<StudentHubConfiguration> DROPWIZARD = IntegrationTestSuite.DROPWIZARD;

  public static Client client = new JerseyClientBuilder(DROPWIZARD.getEnvironment()).build("LoginTest");

  @Test
  public void loginTest() {
    Response response = client.target(
        String.format("http://localhost:%d/api/auth/login", DROPWIZARD.getLocalPort()))
        .queryParam("username", "superadmin@example.com")
        .queryParam("password", "test").request().post(null);
    assertEquals(200, response.getStatus());
    assertNotNull(response.getHeaderString("Authorization"));
  }
}

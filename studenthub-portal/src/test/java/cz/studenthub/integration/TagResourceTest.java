package cz.studenthub.integration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.GenericType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import cz.studenthub.IntegrationTestSuite;
import cz.studenthub.StudentHubConfiguration;
import cz.studenthub.core.Topic;
import cz.studenthub.core.User;
import io.dropwizard.testing.DropwizardTestSupport;

public class TagResourceTest {
  public static DropwizardTestSupport<StudentHubConfiguration> DROPWIZARD;

  private static Client client;

  @BeforeClass
  public void setup() {
      DROPWIZARD = IntegrationTestSuite.DROPWIZARD;
      client = IntegrationTestSuite.BUILDER.build("TagTest");
  }

  @Test(dependsOnGroups = "login")
  public void listUsers() {
    List<User> list = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/tags/Java/users", DROPWIZARD.getLocalPort()))
        .request(), client).get(new GenericType<List<User>>(){});

    assertNotNull(list);
    assertEquals(list.size(), 4);
  }

  @Test(dependsOnGroups = "migrate")
  public void listTopic() {
    List<Topic> list = client.target(String.format("http://localhost:%d/api/tags/Java/topics", DROPWIZARD.getLocalPort()))
        .request()
        .get(new GenericType<List<Topic>>(){});

    assertNotNull(list);
    assertEquals(list.size(), 2);
  }

}
package cz.studenthub.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.GenericType;
import org.junit.Test;

import cz.studenthub.IntegrationTestSuite;
import cz.studenthub.StudentHubConfiguration;
import cz.studenthub.core.Topic;
import cz.studenthub.core.User;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;

public class TagResourceTest {
  public static final DropwizardAppRule<StudentHubConfiguration> DROPWIZARD = IntegrationTestSuite.DROPWIZARD;

  private static Client client = new JerseyClientBuilder(DROPWIZARD.getEnvironment()).build("TagTest");

  @Test
  public void listUsers() {
    List<User> list = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/tags/Java/users", DROPWIZARD.getLocalPort()))
        .request(), client).get(new GenericType<List<User>>(){});

    assertNotNull(list);
    assertEquals(4, list.size());
  }

  @Test
  public void listTopic() {
    List<Topic> list = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/tags/Java/topics", DROPWIZARD.getLocalPort()))
        .request(), client).get(new GenericType<List<Topic>>(){});

    assertNotNull(list);
    assertEquals(2, list.size());
  }

}
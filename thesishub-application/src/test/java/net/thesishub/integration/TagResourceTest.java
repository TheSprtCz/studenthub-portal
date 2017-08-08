package net.thesishub.integration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.GenericType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.dropwizard.testing.DropwizardTestSupport;
import net.thesishub.IntegrationTestSuite;
import net.thesishub.ThesisHubConfiguration;
import net.thesishub.core.Topic;
import net.thesishub.core.User;

public class TagResourceTest {
  private DropwizardTestSupport<ThesisHubConfiguration> dropwizard;

  private Client client;

  @BeforeClass
  public void setup() {
      dropwizard = IntegrationTestSuite.DROPWIZARD;
      client = IntegrationTestSuite.BUILDER.build("TagTest");
  }

  @Test(dependsOnGroups = "login")
  public void listUsers() {
    List<User> list = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/tags/Java/users", dropwizard.getLocalPort()))
        .request(), client).get(new GenericType<List<User>>(){});

    assertNotNull(list);
    assertEquals(list.size(), 4);
  }

  @Test(dependsOnGroups = "migrate")
  public void listTopic() {
    List<Topic> list = client.target(String.format("http://localhost:%d/api/tags/Java/topics", dropwizard.getLocalPort()))
        .request()
        .get(new GenericType<List<Topic>>(){});

    assertNotNull(list);
    assertEquals(list.size(), 2);
  }

}
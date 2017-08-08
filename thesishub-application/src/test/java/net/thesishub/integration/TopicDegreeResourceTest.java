package net.thesishub.integration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.dropwizard.testing.DropwizardTestSupport;
import net.minidev.json.JSONObject;
import net.thesishub.IntegrationTestSuite;
import net.thesishub.ThesisHubConfiguration;
import net.thesishub.core.TopicDegree;

public class TopicDegreeResourceTest {
  private DropwizardTestSupport<ThesisHubConfiguration> dropwizard;
  private Client client;

  @BeforeClass
  public void setup() {
      dropwizard = IntegrationTestSuite.DROPWIZARD;
      client = IntegrationTestSuite.BUILDER.build("TopicDegreeTest");
  }

  private List<TopicDegree> fetchTopicDegrees() {
    return client.target(String.format("http://localhost:%d/api/degrees/", dropwizard.getLocalPort()))
      .request().get(new GenericType<List<TopicDegree>>(){});
  }

  @Test(dependsOnGroups = "migrate")
  public void listTopicDegrees() {
    List<TopicDegree> list = fetchTopicDegrees();

    assertNotNull(list);
    assertEquals(list.size(), 5);
  }

  @Test(dependsOnGroups = "migrate")
  public void fetchTopicDegree() {
    TopicDegree degree = client.target(String.format("http://localhost:%d/api/degrees/HIGH_SCHOOL", dropwizard.getLocalPort()))
        .request(MediaType.APPLICATION_JSON).get(TopicDegree.class);

    assertNotNull(degree);
    assertEquals(degree.getDescription(), "High school");
  }

  @Test(dependsOnMethods = "listTopicDegrees", dependsOnGroups = "login")
  public void createTopicDegree() {
    JSONObject degree = new JSONObject();
    degree.put("name", "Another");
    
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/degrees", dropwizard.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).post(Entity.json(degree.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 201);
    assertEquals(fetchTopicDegrees().size(), 6);
  }

  @Test(dependsOnMethods = "createTopicDegree")
  public void updateTopicDegree() {
    JSONObject degree = new JSONObject();
    degree.put("name", "Another");
    degree.put("description", "test");

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/degrees/Another", dropwizard.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).put(Entity.json(degree.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 200);
    assertEquals(response.readEntity(TopicDegree.class).getDescription(), "test");
  }

  @Test(dependsOnMethods = "updateTopicDegree")
  public void deleteTopicDegree() {
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/degrees/Another", dropwizard.getLocalPort())).request(), client)
      .delete();
    List<TopicDegree> list = fetchTopicDegrees();

    assertNotNull(response);
    assertEquals(response.getStatus(), 204);
    assertEquals(list.size(), 5);
  }

}
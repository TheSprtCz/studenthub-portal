package cz.studenthub.integration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import cz.studenthub.IntegrationTestSuite;
import cz.studenthub.StudentHubConfiguration;
import cz.studenthub.core.Topic;
import cz.studenthub.core.TopicApplication;
import cz.studenthub.core.User;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.DropwizardTestSupport;
import net.minidev.json.JSONObject;

public class TopicResourceTest {
  public static DropwizardTestSupport<StudentHubConfiguration> DROPWIZARD;

  private static Client client;

  @BeforeClass
  public void setup() {
      DROPWIZARD = IntegrationTestSuite.DROPWIZARD;
      client = new JerseyClientBuilder(DROPWIZARD.getEnvironment()).build("TopicTest");
  }
  
  private List<Topic> fetchTopics() {
    return IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/topics", DROPWIZARD.getLocalPort()))
      .request(), client).get(new GenericType<List<Topic>>(){});
  }

  private List<User> getSupervisors(int id) {
    return IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/topics/%d/supervisors", DROPWIZARD.getLocalPort(), id))
      .request(), client).get(new GenericType<List<User>>(){});
  }

  @Test(dependsOnGroups = "migrate")
  public void listTopics() {
    List<Topic> list = fetchTopics();

    assertNotNull(list);
    assertEquals(list.size(), 5);
  }

  @Test
  public void fetchTopic() {
    Topic topic = client.target(String.format("http://localhost:%d/api/topics/2", DROPWIZARD.getLocalPort()))
        .request(MediaType.APPLICATION_JSON)
        .get(Topic.class);

    assertNotNull(topic);
    assertEquals(topic.getTitle(), "Dropwizard");
  }

  @Test(dependsOnMethods = "listTopics")
  public void createTopic() {
    JSONObject creator = new JSONObject();
    creator.put("id", 11);

    JSONObject topic = new JSONObject();
    topic.put("title", "New Topic");
    topic.put("creator", creator);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/topics", DROPWIZARD.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).post(Entity.json(topic.toJSONString()));

    assertNotNull(response);
    System.out.println(response);
    assertEquals(response.getStatus(), 201);
    assertEquals(fetchTopics().size(), 6);
    assertEquals((long) response.readEntity(Topic.class).getId(), 6);
  }

  @Test(dependsOnMethods = "createTopic")
  public void updateTopic() {
    JSONObject creator = new JSONObject();
    creator.put("id", 11);

    JSONObject topic = new JSONObject();
    topic.put("title", "Another Topic");
    topic.put("creator", creator);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/topics/6", DROPWIZARD.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).put(Entity.json(topic.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 200);
    assertEquals(response.readEntity(Topic.class).getTitle(), "Another Topic");
  }

  @Test(dependsOnMethods = "updateTopic")
  public void deleteTopic() {
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/topics/6", DROPWIZARD.getLocalPort()))
      .request(), client).delete();

    assertNotNull(response);
    assertEquals(response.getStatus(), 204);
    assertEquals(fetchTopics().size(), 5);
  }

  @Test(dependsOnGroups = "login")
  public void superviseTopic() {
    User superadmin = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/users/19", DROPWIZARD.getLocalPort())).request(), client)
        .get(User.class);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/topics/1/supervise", DROPWIZARD.getLocalPort())).request(), client)
        .put(Entity.json(""));

    List<User> supervisors = getSupervisors(1);

    assertNotNull(response);
    assertEquals(response.getStatus(), 200);
    assertTrue(supervisors.contains(superadmin));
  }

  @Test(dependsOnGroups = "login")
  public void fetchSupervisors() {
    List<User> supervisors = getSupervisors(2);

    assertNotNull(supervisors);
    assertEquals(supervisors.size(), 3);
  }

  @Test(dependsOnGroups = "login")
  public void fetchApplications() {
    List<TopicApplication> apps = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/topics/1/applications", DROPWIZARD.getLocalPort()))
        .request(), client).get(new GenericType<List<TopicApplication>>(){});

    assertNotNull(apps);
    assertEquals(apps.size(), 3);
  }

  @Test(dependsOnGroups = "login")
  public void fetchCreator() {
    User creator = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/topics/2/creator", DROPWIZARD.getLocalPort()))
        .request(), client).get(User.class);

    assertNotNull(creator);
    assertEquals((long) creator.getId(), 11);
  }

  @Test(dependsOnGroups = "migrate")
  public void search() {
    List<Topic> topics = client.target(String.format("http://localhost:%d/api/topics/search", DROPWIZARD.getLocalPort()))
        .queryParam("text", "java")
        .request().get(new GenericType<List<Topic>>(){});

    assertNotNull(topics);
    assertEquals(topics.size(), 3);

    topics = client.target(String.format("http://localhost:%d/api/topics/search", DROPWIZARD.getLocalPort()))
        .queryParam("text", "Dropwizard")
        .request().get(new GenericType<List<Topic>>(){});

    assertNotNull(topics);
    assertEquals(topics.size(), 1);

     topics = client.target(String.format("http://localhost:%d/api/topics/search", DROPWIZARD.getLocalPort()))
        .queryParam("text", "UI")
        .request().get(new GenericType<List<Topic>>(){});

    assertNotNull(topics);
    assertEquals(topics.size(), 1);
  }
}
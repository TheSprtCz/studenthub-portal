package cz.studenthub.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import cz.studenthub.IntegrationTestSuite;
import cz.studenthub.StudentHubConfiguration;
import cz.studenthub.core.Topic;
import cz.studenthub.core.TopicApplication;
import cz.studenthub.core.User;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;
import net.minidev.json.JSONObject;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TopicResourceTest {
  public static final DropwizardAppRule<StudentHubConfiguration> DROPWIZARD = IntegrationTestSuite.DROPWIZARD;

  private static Client client = new JerseyClientBuilder(DROPWIZARD.getEnvironment()).build("TopicTest");
  
  private List<Topic> fetchTopics() {
    return client.target(String.format("http://localhost:%d/api/topics", DROPWIZARD.getLocalPort()))
      .request().get(new GenericType<List<Topic>>(){});
  }

  private List<User> getSupervisors(int id) {
    return IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/topics/%d/supervisors", DROPWIZARD.getLocalPort(), id))
      .request(), client).get(new GenericType<List<User>>(){});
  }

  @Test
  public void listTopics() {
    List<Topic> list = fetchTopics();

    assertNotNull(list);
    assertEquals(4, list.size());
  }

  @Test
  public void fetchTopic() {
    Topic topic = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/topics/2", DROPWIZARD.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(Topic.class);

    assertNotNull(topic);
    assertEquals("Dropwizard", topic.getTitle());
  }

  @Test
  public void createTopic() {
    JSONObject creator = new JSONObject();
    creator.put("id", 9);

    JSONObject topic = new JSONObject();
    topic.put("title", "New Topic");
    topic.put("creator", creator);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/topics", DROPWIZARD.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).post(Entity.json(topic.toJSONString()));

    assertNotNull(response);
    System.out.println(response);
    assertEquals(201, response.getStatus());
    assertEquals(5, fetchTopics().size());
  }

  @Test
  public void updateTopic() {
    JSONObject creator = new JSONObject();
    creator.put("id", 9);

    JSONObject topic = new JSONObject();
    topic.put("title", "Another Topic");
    topic.put("creator", creator);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/topics/2", DROPWIZARD.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).put(Entity.json(topic.toJSONString()));

    assertNotNull(response);
    assertEquals(200, response.getStatus());
    assertEquals("Another Topic", response.readEntity(Topic.class).getTitle());
  }

  @Test
  public void deleteTopic() {
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/topics/5", DROPWIZARD.getLocalPort())).request(), client)
      .delete();

    assertNotNull(response);
    assertEquals(204, response.getStatus());
    assertEquals(4, fetchTopics().size());
  }

  @Test
  public void superviseTopic() {
    User superadmin = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/users/19", DROPWIZARD.getLocalPort())).request(), client)
        .get(User.class);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/topics/1/supervise", DROPWIZARD.getLocalPort())).request(), client)
        .put(Entity.json(""));

    List<User> supervisors = getSupervisors(1);

    assertNotNull(response);
    assertEquals(200, response.getStatus());
    assertTrue(supervisors.contains(superadmin));
  }

  @Test
  public void fetchSupervisors() {
    List<User> supervisors = getSupervisors(2);

    assertNotNull(supervisors);
    assertEquals(3, supervisors.size());
  }

  @Test
  public void fetchApplications() {
    List<TopicApplication> apps = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/topics/1/applications", DROPWIZARD.getLocalPort()))
        .request(), client).get(new GenericType<List<TopicApplication>>(){});

    assertNotNull(apps);
    assertEquals(3, apps.size());
  }

  @Test
  public void fetchCreator() {
    User creator = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/topics/2/creator", DROPWIZARD.getLocalPort()))
        .request(), client).get(User.class);

    assertNotNull(creator);
    assertEquals((long) 11, (long) creator.getId());
  }

  @Test
  public void search() {
    List<Topic> topics = client.target(String.format("http://localhost:%d/api/topics/search", DROPWIZARD.getLocalPort()))
        .queryParam("text", "java")
        .request().get(new GenericType<List<Topic>>(){});

    assertNotNull(topics);
    assertEquals(3, topics.size());

    topics = client.target(String.format("http://localhost:%d/api/topics/search", DROPWIZARD.getLocalPort()))
        .queryParam("text", "Dropwizard")
        .request().get(new GenericType<List<Topic>>(){});

    assertNotNull(topics);
    assertEquals(1, topics.size());

     topics = client.target(String.format("http://localhost:%d/api/topics/search", DROPWIZARD.getLocalPort()))
        .queryParam("text", "UI")
        .request().get(new GenericType<List<Topic>>(){});

    assertNotNull(topics);
    assertEquals(1, topics.size());
  }
}
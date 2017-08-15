package net.thesishub.integration;

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

import io.dropwizard.testing.DropwizardTestSupport;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.thesishub.IntegrationTestSuite;
import net.thesishub.ThesisHubConfiguration;
import net.thesishub.core.Project;
import net.thesishub.core.Topic;
import net.thesishub.core.TopicApplication;
import net.thesishub.core.User;
import net.thesishub.db.TopicDAOTest;

public class TopicResourceTest {
  private DropwizardTestSupport<ThesisHubConfiguration> dropwizard;
  private Client client;

  @BeforeClass
  public void setup() {
      dropwizard = IntegrationTestSuite.DROPWIZARD;
      client = IntegrationTestSuite.BUILDER.build("TopicTest");
  }
  
  private List<Topic> fetchTopics() {
    return IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/topics", dropwizard.getLocalPort()))
      .request(), client).get(new GenericType<List<Topic>>(){});
  }

  private List<User> getSupervisors(int id) {
    return IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/topics/%d/supervisors", dropwizard.getLocalPort(), id))
      .request(), client).get(new GenericType<List<User>>(){});
  }

  @Test(dependsOnGroups = "login")
  public void listTopics() {
    List<Topic> list = fetchTopics();

    assertNotNull(list);
    assertEquals(list.size(), TopicDAOTest.COUNT);
  }

  @Test(dependsOnGroups = "migrate", groups = "fetchTopic")
  public void fetchTopic() {
    Topic topic = client.target(String.format("http://localhost:%d/api/topics/2", dropwizard.getLocalPort()))
        .request(MediaType.APPLICATION_JSON)
        .get(Topic.class);

    assertNotNull(topic);
    assertEquals(topic.getTitle(), "Dropwizard");
  }

  @Test(dependsOnMethods = "listTopics")
  public void createTopic() {
    JSONObject degree = new JSONObject();
    degree.put("name", "HIGH_SCHOOL");

    JSONArray degrees = new JSONArray();
    degrees.add(degree);
    
    JSONObject creator = new JSONObject();
    creator.put("id", 11);

    JSONObject topic = new JSONObject();
    topic.put("title", "New Topic");
    topic.put("creator", creator);
    topic.put("degrees", degrees);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/topics", dropwizard.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).post(Entity.json(topic.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 201);
    assertEquals(fetchTopics().size(), 6);
    assertEquals((long) response.readEntity(Topic.class).getId(), TopicDAOTest.COUNT + 1);
  }

  @Test(dependsOnMethods = "createTopic")
  public void updateTopic() {
    JSONObject degree = new JSONObject();
    degree.put("name", "HIGH_SCHOOL");

    JSONArray degrees = new JSONArray();
    degrees.add(degree);

    JSONObject creator = new JSONObject();
    creator.put("id", 11);

    JSONObject topic = new JSONObject();
    topic.put("title", "Another Topic");
    topic.put("creator", creator);
    topic.put("degrees", degrees);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/topics/6", dropwizard.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).put(Entity.json(topic.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 200);
    assertEquals(response.readEntity(Topic.class).getTitle(), "Another Topic");
  }

  @Test(dependsOnMethods = "updateTopic")
  public void deleteTopic() {
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/topics/6", dropwizard.getLocalPort()))
      .request(), client).delete();

    assertNotNull(response);
    assertEquals(response.getStatus(), 204);
    assertEquals(fetchTopics().size(), TopicDAOTest.COUNT);
  }

  @Test(dependsOnGroups = "login")
  public void superviseTopic() {
    User superadmin = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/users/19", dropwizard.getLocalPort())).request(), client)
        .get(User.class);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/topics/1/supervise", dropwizard.getLocalPort())).request(), client)
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
    List<TopicApplication> apps = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/topics/1/applications", dropwizard.getLocalPort()))
        .request(), client).get(new GenericType<List<TopicApplication>>(){});

    assertNotNull(apps);
    assertEquals(apps.size(), 3);
  }

  @Test(dependsOnGroups = "login")
  public void fetchCreator() {
    User creator = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/topics/2/creator", dropwizard.getLocalPort()))
        .request(), client).get(User.class);

    assertNotNull(creator);
    assertEquals((long) creator.getId(), 11);
  }

  @Test(dependsOnGroups = "migrate")
  public void search() {
    List<Topic> topics = client.target(String.format("http://localhost:%d/api/topics/search", dropwizard.getLocalPort()))
        .queryParam("text", "java")
        .request().get(new GenericType<List<Topic>>(){});

    assertNotNull(topics);
    assertEquals(topics.size(), 3);

    topics = client.target(String.format("http://localhost:%d/api/topics/search", dropwizard.getLocalPort()))
        .queryParam("text", "Dropwizard")
        .request().get(new GenericType<List<Topic>>(){});

    assertNotNull(topics);
    assertEquals(topics.size(), 1);

     topics = client.target(String.format("http://localhost:%d/api/topics/search", dropwizard.getLocalPort()))
        .queryParam("text", "UI")
        .request().get(new GenericType<List<Topic>>(){});

    assertNotNull(topics);
    assertEquals(topics.size(), 1);
  }

  @Test(dependsOnGroups = "migrate")
  public void fetchProjects() {
    List<Project> projects = client.target(String.format("http://localhost:%d/api/topics/2/projects", dropwizard.getLocalPort())).request()
        .get(new GenericType<List<Project>>(){}); 

    assertNotNull(projects);
    assertEquals(projects.size(), 1);
    assertEquals((long) projects.get(0).getId(), 1);
  }

  @Test(dependsOnGroups = "migrate")
  public void fetchHighlighted() {
    List<Topic> topics = client.target(String.format("http://localhost:%d/api/topics/highlighted", dropwizard.getLocalPort())).request()
        .get(new GenericType<List<Topic>>(){}); 

    assertNotNull(topics);
    assertEquals(topics.size(), 3);

    // Test if queryParam is working
    topics = client.target(String.format("http://localhost:%d/api/topics/highlighted?count=2", dropwizard.getLocalPort())).request()
        .get(new GenericType<List<Topic>>(){}); 

    assertNotNull(topics);
    assertEquals(topics.size(), 2);
  }

  @Test(dependsOnGroups = "migrate")
  public void fetchRecent() {
    List<Topic> topics = client.target(String.format("http://localhost:%d/api/topics/recent", dropwizard.getLocalPort())).request()
        .get(new GenericType<List<Topic>>(){}); 

    assertNotNull(topics);
    assertEquals(topics.size(), 4);

    // Test if queryParam is working
    topics = client.target(String.format("http://localhost:%d/api/topics/recent?count=2", dropwizard.getLocalPort())).request()
        .get(new GenericType<List<Topic>>(){}); 

    assertNotNull(topics);
    assertEquals(topics.size(), 2);
  }

}
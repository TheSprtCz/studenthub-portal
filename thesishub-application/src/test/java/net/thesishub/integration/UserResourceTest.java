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
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.thesishub.IntegrationTestSuite;
import net.thesishub.ThesisHubConfiguration;
import net.thesishub.core.Project;
import net.thesishub.core.Topic;
import net.thesishub.core.TopicApplication;
import net.thesishub.core.User;

public class UserResourceTest {
  
  private static final int COUNT = 23;
  private DropwizardTestSupport<ThesisHubConfiguration> dropwizard;
  private Client client;

  @BeforeClass
  public void setup() {
      dropwizard = IntegrationTestSuite.DROPWIZARD;
      client = IntegrationTestSuite.BUILDER.build("UserTest");
  }
  
  private List<User> fetchUsers() {
    return IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/users", dropwizard.getLocalPort()))
      .request(), client).get(new GenericType<List<User>>(){});
  }

  @Test(dependsOnGroups = {"login", "invite"})
  public void listUsers() {
    List<User> list = fetchUsers();

    assertNotNull(list);
    assertEquals(list.size(), COUNT);
  }

  @Test(dependsOnGroups = "login")
  public void fetchUser() {
    User user = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/users/2", dropwizard.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(User.class);

    assertNotNull(user);
    assertEquals(user.getUsername(), "supervisor1");
  }

  @Test(dependsOnMethods = "listUsers")
  public void updateUser() {
    JSONObject user = new JSONObject();
    user.put("username", "notused");
    user.put("name", "Unknown Unknown");
    user.put("email", "xyzuipo@example.com");
    user.put("phone", "111 111 111");
    JSONArray roles = new JSONArray();
    roles.add("ADMIN");
    user.put("roles", roles);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/users/1", dropwizard.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).put(Entity.json(user.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 200);
    assertEquals(response.readEntity(User.class).getPhone(), "111 111 111");
  }

  @Test(dependsOnMethods = "updateUser")
  public void deleteUser() {
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/users/1", dropwizard.getLocalPort())).request(), client)
      .delete();

    assertNotNull(response);
    assertEquals(response.getStatus(), 204);
    assertEquals(fetchUsers().size(), COUNT - 1);
  }

  @Test(dependsOnGroups = "login")
  public void fetchApplications() {
    List<TopicApplication> apps = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/users/13/applications", dropwizard.getLocalPort())).request(), client)
        .get(new GenericType<List<TopicApplication>>(){}); 

    assertNotNull(apps);
    assertEquals(apps.size(), 3);
  }

  @Test(dependsOnGroups = "login")
  public void fetchLedApplications() {
    List<TopicApplication> apps = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/users/8/ledApplications", dropwizard.getLocalPort())).request(), client)
        .get(new GenericType<List<TopicApplication>>(){}); 

    assertNotNull(apps);
    assertEquals(apps.size(), 2);
  }

  @Test(dependsOnGroups = "login")
  public void fetchSupervisedApplications() {
    List<TopicApplication> apps = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/users/2/supervisedApplications", dropwizard.getLocalPort())).request(), client)
        .get(new GenericType<List<TopicApplication>>(){}); 

    assertNotNull(apps);
    assertEquals(apps.size(), 2);
  }

  @Test(dependsOnGroups = "login")
  public void fetchSupervisedTopics() {
    List<Topic> topics = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/users/7/supervisedTopics", dropwizard.getLocalPort())).request(), client)
        .get(new GenericType<List<Topic>>(){}); 

    assertNotNull(topics);
    assertEquals(topics.size(), 3);
  }

  @Test(dependsOnGroups = "login")
  public void fetchOwnedTopics() {
    List<Topic> topics = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/users/9/ownedTopics", dropwizard.getLocalPort())).request(), client)
        .get(new GenericType<List<Topic>>(){}); 

    assertNotNull(topics);
    assertEquals(topics.size(), 2);
  }

  @Test(dependsOnGroups = "login")
  public void fetchOwnedProjects() {
    List<Project> projects = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/users/9/projects", dropwizard.getLocalPort())).request(), client)
        .get(new GenericType<List<Project>>(){}); 

    assertNotNull(projects);
    assertEquals(projects.size(), 1);
    assertEquals((long) projects.get(0).getId(), 1);
  }
}
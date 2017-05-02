package cz.studenthub.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserResourceTest {
  public static final DropwizardAppRule<StudentHubConfiguration> DROPWIZARD = IntegrationTestSuite.DROPWIZARD;

  private static Client client = new JerseyClientBuilder(DROPWIZARD.getEnvironment()).build("UserTest");
  
  private List<User> fetchUsers() {
    return IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/users", DROPWIZARD.getLocalPort()))
      .request(), client).get(new GenericType<List<User>>(){});
  }

  @Test
  public void listUsers() {
    List<User> list = fetchUsers();

    assertNotNull(list);
    assertEquals(19, list.size());
  }

  @Test
  public void fetchUser() {
    User user = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/users/2", DROPWIZARD.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(User.class);

    assertNotNull(user);
    assertEquals("supervisor1", user.getUsername());
  }

  @Test
  public void createUser() {
    JSONObject user = new JSONObject();
    user.put("username", "unknown");
    user.put("name", "Unknown Unknown");
    user.put("password", "test");
    user.put("email", "test@example.com");
    JSONArray roles = new JSONArray();
    roles.add("ADMIN");
    user.put("roles", roles);

    Response response = client.target(String.format("http://localhost:%d/api/users/signUp", DROPWIZARD.getLocalPort()))
      .request(MediaType.APPLICATION_JSON).post(Entity.json(user.toJSONString()));

    assertNotNull(response);
    assertEquals(201, response.getStatus());
    assertEquals(20, fetchUsers().size());
  }

  @Test
  public void updateUser() {
    JSONObject user = new JSONObject();
    user.put("username", "notused");
    user.put("name", "Unknown Unknown");
    user.put("password", "test");
    user.put("email", "xyzuipo@example.com");
    user.put("phone", "111 111 111");
    JSONArray roles = new JSONArray();
    roles.add("ADMIN");
    user.put("roles", roles);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/users/1", DROPWIZARD.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).put(Entity.json(user.toJSONString()));

    assertNotNull(response);
    assertEquals(200, response.getStatus());
    assertEquals("111 111 111", response.readEntity(User.class).getPhone());
  }

  @Test
  public void deleteUser() {
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/users/20", DROPWIZARD.getLocalPort())).request(), client)
      .delete();

    assertNotNull(response);
    assertEquals(204, response.getStatus());
    assertEquals(19, fetchUsers().size());
  }

  @Test
  public void fetchApplications() {
    List<TopicApplication> apps = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/users/13/applications", DROPWIZARD.getLocalPort())).request(), client)
        .get(new GenericType<List<TopicApplication>>(){}); 

    assertNotNull(apps);
    assertEquals(3, apps.size());
  }

  @Test
  public void fetchLeadApplications() {
    List<TopicApplication> apps = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/users/8/leadApplications", DROPWIZARD.getLocalPort())).request(), client)
        .get(new GenericType<List<TopicApplication>>(){}); 

    assertNotNull(apps);
    assertEquals(2, apps.size());
  }

  @Test
  public void fetchSupervisedApplications() {
    List<TopicApplication> apps = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/users/2/supervisedApplications", DROPWIZARD.getLocalPort())).request(), client)
        .get(new GenericType<List<TopicApplication>>(){}); 

    assertNotNull(apps);
    assertEquals(2, apps.size());
  }

  @Test
  public void fetchSupervisedTopics() {
    List<Topic> topics = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/users/7/supervisedTopics", DROPWIZARD.getLocalPort())).request(), client)
        .get(new GenericType<List<Topic>>(){}); 

    assertNotNull(topics);
    assertEquals(2, topics.size());
  }

  @Test
  public void fetchOwnedTopics() {
    List<Topic> topics = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/users/9/ownedTopics", DROPWIZARD.getLocalPort())).request(), client)
        .get(new GenericType<List<Topic>>(){}); 

    assertNotNull(topics);
    assertEquals(2, topics.size());
  }
}
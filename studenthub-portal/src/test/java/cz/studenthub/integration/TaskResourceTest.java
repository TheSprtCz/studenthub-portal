package cz.studenthub.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import cz.studenthub.IntegrationTestSuite;
import cz.studenthub.StudentHubConfiguration;
import cz.studenthub.core.Task;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;
import net.minidev.json.JSONObject;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TaskResourceTest {
  public static final DropwizardAppRule<StudentHubConfiguration> DROPWIZARD = IntegrationTestSuite.DROPWIZARD;

  private static Client client = new JerseyClientBuilder(DROPWIZARD.getEnvironment()).build("TaskTest");

  @Test
  public void fetchTask() {
    Task task = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/tasks/1", DROPWIZARD.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(Task.class);

    assertNotNull(task);
    assertEquals("Reduce size", task.getTitle());
  }

  @Test
  public void createTask() {
    JSONObject application = new JSONObject();
    application.put("id", 1);

    JSONObject app = new JSONObject();
    app.put("title", "Something");
    app.put("application", application);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/tasks", DROPWIZARD.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).post(Entity.json(app.toJSONString()));

    assertNotNull(response);
    assertEquals(201, response.getStatus());
    assertEquals(4, TopicApplicationResourceTest.fetchTasks().size());
  }

  @Test
  public void updateTask() {
    JSONObject application = new JSONObject();
    application.put("id", 1);

    JSONObject app = new JSONObject();
    app.put("title", "New");
    app.put("application", application);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/tasks/6", DROPWIZARD.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).put(Entity.json(app.toJSONString()));

    assertNotNull(response);
    assertEquals(200, response.getStatus());

    Task task = response.readEntity(Task.class);
    assertEquals("New", task.getTitle());
    assertEquals(1, (long) task.getApplication().getId());
  }

  @Test
  public void deleteTask() {
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/tasks/7", DROPWIZARD.getLocalPort())).request(), client)
      .delete();

    assertNotNull(response);
    assertEquals(204, response.getStatus());
    assertEquals(3, TopicApplicationResourceTest.fetchTasks().size());
  }
}
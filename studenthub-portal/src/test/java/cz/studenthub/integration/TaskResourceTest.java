package cz.studenthub.integration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import cz.studenthub.IntegrationTestSuite;
import cz.studenthub.StudentHubConfiguration;
import cz.studenthub.core.Task;
import io.dropwizard.testing.DropwizardTestSupport;
import net.minidev.json.JSONObject;

public class TaskResourceTest {
  public static DropwizardTestSupport<StudentHubConfiguration> DROPWIZARD;

  private static Client client;

  @BeforeClass
  public void setup() {
      DROPWIZARD = IntegrationTestSuite.DROPWIZARD;
      client = IntegrationTestSuite.BUILDER.build("TaskTest");
  }

  @Test(dependsOnGroups = "login")
  public void fetchTask() {
    Task task = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/tasks/1", DROPWIZARD.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(Task.class);

    assertNotNull(task);
    assertEquals(task.getTitle(), "Reduce size");
  }

  @Test(dependsOnGroups = "login")
  public void createTask() {
    JSONObject application = new JSONObject();
    application.put("id", 1);

    JSONObject app = new JSONObject();
    app.put("title", "Something");
    app.put("application", application);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/tasks", DROPWIZARD.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).post(Entity.json(app.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 201);
    assertEquals(TopicApplicationResourceTest.fetchTasks().size(), 4);
  }

  @Test(dependsOnMethods = "createTask")
  public void updateTask() {
    JSONObject application = new JSONObject();
    application.put("id", 1);

    JSONObject app = new JSONObject();
    app.put("title", "New");
    app.put("application", application);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/tasks/7", DROPWIZARD.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).put(Entity.json(app.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 200);

    Task task = response.readEntity(Task.class);
    assertEquals(task.getTitle(), "New");
    assertEquals((long) task.getApplication().getId(), 1);
  }

  @Test(dependsOnMethods = "updateTask")
  public void deleteTask() {
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/tasks/7", DROPWIZARD.getLocalPort())).request(), client)
      .delete();

    assertNotNull(response);
    assertEquals(response.getStatus(), 204);
    assertEquals(TopicApplicationResourceTest.fetchTasks().size(), 3);
  }
}
package net.thesishub.integration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.dropwizard.testing.DropwizardTestSupport;
import net.minidev.json.JSONObject;
import net.thesishub.IntegrationTestSuite;
import net.thesishub.ThesisHubConfiguration;
import net.thesishub.core.Task;
import net.thesishub.db.TaskDAOTest;

public class TaskResourceTest {
  private DropwizardTestSupport<ThesisHubConfiguration> dropwizard;
  private Client client;

  @BeforeClass
  public void setup() {
      dropwizard = IntegrationTestSuite.DROPWIZARD;
      client = IntegrationTestSuite.BUILDER.build("TaskTest");
  }

  @Test(dependsOnGroups = "login")
  public void fetchTask() {
    Task task = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/tasks/1", dropwizard.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(Task.class);

    assertNotNull(task);
    assertEquals(task.getTitle(), "Reduce size");
  }

  @Test(dependsOnGroups = {"login", "listTasks"})
  public void createTask() {
    JSONObject application = new JSONObject();
    application.put("id", 1);

    JSONObject app = new JSONObject();
    app.put("title", "Something");
    app.put("application", application);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/tasks", dropwizard.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).post(Entity.json(app.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 201);
    assertEquals(TopicApplicationResourceTest.fetchTasks().size(), TaskDAOTest.COUNT + 1);
  }

  @Test(dependsOnMethods = "createTask")
  public void updateTask() {
    JSONObject application = new JSONObject();
    application.put("id", 1);

    JSONObject app = new JSONObject();
    app.put("title", "New");
    app.put("application", application);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/tasks/7", dropwizard.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).put(Entity.json(app.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 200);

    Task task = response.readEntity(Task.class);
    assertEquals(task.getTitle(), "New");
    assertEquals((long) task.getApplication().getId(), 1);
  }

  @Test(dependsOnMethods = "updateTask")
  public void deleteTask() {
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/tasks/7", dropwizard.getLocalPort())).request(), client)
      .delete();

    assertNotNull(response);
    assertEquals(response.getStatus(), 204);
    assertEquals(TopicApplicationResourceTest.fetchTasks().size(), TaskDAOTest.COUNT);
  }
}
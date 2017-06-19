package net.thesishub.integration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.icegreen.greenmail.util.GreenMail;

import io.dropwizard.testing.DropwizardTestSupport;
import net.minidev.json.JSONObject;
import net.thesishub.IntegrationTestSuite;
import net.thesishub.ThesisHubConfiguration;
import net.thesishub.core.Task;
import net.thesishub.db.TaskDAOTest;

public class TaskResourceTest {
  private DropwizardTestSupport<ThesisHubConfiguration> dropwizard;
  private Client client;
  private GreenMail greenMail;

  @BeforeClass
  public void setup() {
      dropwizard = IntegrationTestSuite.DROPWIZARD;
      client = IntegrationTestSuite.BUILDER.build("TaskTest");
      greenMail = IntegrationTestSuite.MAIL;
  }

  @BeforeMethod
  public void reset() {
    greenMail.reset();
  }

  @Test(dependsOnGroups = "login")
  public void fetchTask() {
    Task task = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/tasks/1", dropwizard.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(Task.class);

    assertNotNull(task);
    assertEquals(task.getTitle(), "Reduce size");
  }

  @Test(dependsOnGroups = {"login", "listTasks", "testMail"})
  public void createTask() throws MessagingException {
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

    // Test that all emails have arrived
    assertTrue(greenMail.waitForIncomingEmail(IntegrationTestSuite.MAIL_TIMEOUT, 3));
    Message[] messages = greenMail.getReceivedMessages();
    assertEquals(messages.length, 3);

    // Test that correct email arrived to correct person (TaskCreated to student, techLeader and supervisor)
    String[] emails = {"student1@example.com", "leader1@example.com", "supervisor5@example.com" };
    Set<String> emailSet = new HashSet<String>(Arrays.asList(emails));
    for (Message msg : messages) {
      assertFalse(IntegrationTestSuite.hasUnfilledArguments(msg));
      assertEquals(msg.getSubject(), "New task was created");
      assertTrue(emailSet.contains(msg.getAllRecipients()[0].toString()));
    }
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
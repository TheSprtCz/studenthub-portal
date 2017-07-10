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
import net.thesishub.core.ApplicationStatus;
import net.thesishub.core.Task;
import net.thesishub.core.TopicApplication;
import net.thesishub.db.TopicApplicationDAOTest;

import net.minidev.json.JSONArray;

public class TopicApplicationResourceTest {
  private static DropwizardTestSupport<ThesisHubConfiguration> DROPWIZARD;
  private static Client CLIENT;

  @BeforeClass
  public void setup() {
      DROPWIZARD = IntegrationTestSuite.DROPWIZARD;
      CLIENT = IntegrationTestSuite.BUILDER.build("TopicApplicationTest");
  }

  private TopicApplication fetchApplication(int id) {
    return IntegrationTestSuite.authorizedRequest(CLIENT.target(String.format("http://localhost:%d/api/applications/%d", DROPWIZARD.getLocalPort(), id))
    .request(MediaType.APPLICATION_JSON), CLIENT).get(TopicApplication.class);
  }
  
  private List<TopicApplication> fetchApplications() {
    return IntegrationTestSuite.authorizedRequest(CLIENT.target(String.format("http://localhost:%d/api/applications", DROPWIZARD.getLocalPort()))
      .request(), CLIENT).get(new GenericType<List<TopicApplication>>(){});
  }

  public static List<Task> fetchTasks() {
    return IntegrationTestSuite.authorizedRequest(CLIENT.target(String.format("http://localhost:%d/api/applications/1/tasks", DROPWIZARD.getLocalPort()))
      .request(), CLIENT).get(new GenericType<List<Task>>(){});
  }

  @Test(dependsOnGroups = "login")
  public void listApplications() {
    List<TopicApplication> list = fetchApplications();

    assertNotNull(list);
    assertEquals(list.size(), TopicApplicationDAOTest.COUNT);
  }

  @Test(dependsOnGroups = "login")
  public void fetchApplication() {
    TopicApplication app = fetchApplication(6);

    assertNotNull(app);
    assertEquals(app.getOfficialAssignment(), ".JSX editor plugin");
  }

  @Test(dependsOnMethods = "listApplications")
  public void createApplication() {
    JSONObject degree = new JSONObject();
    degree.put("name", "HIGH_SCHOOL");
    JSONObject faculty = new JSONObject();
    faculty.put("id", 2);
    JSONObject topic = new JSONObject();
    topic.put("id", 2);
    JSONObject student = new JSONObject();
    student.put("id", 19);

    JSONObject app = new JSONObject();
    app.put("faculty", faculty);
    app.put("topic", topic);
    app.put("student", student);
    app.put("degree", degree);
    app.put("status", "WAITING_APPROVAL");

    Response response = IntegrationTestSuite.authorizedRequest(CLIENT.target(String.format("http://localhost:%d/api/applications", DROPWIZARD.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), CLIENT).post(Entity.json(app.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 201);
    assertEquals(fetchApplications().size(), TopicApplicationDAOTest.COUNT + 1);
  }

  @Test(dependsOnMethods = "createApplication")
  public void updateApplication() {
    JSONObject degree = new JSONObject();
    degree.put("name", "HIGH_SCHOOL");
    JSONObject faculty = new JSONObject();
    faculty.put("id", 2);
    JSONObject topic = new JSONObject();
    topic.put("id", 2);
    JSONObject student = new JSONObject();
    student.put("id", 12);

    JSONObject app = new JSONObject();
    app.put("officialAssignment", "New");
    app.put("faculty", faculty);
    app.put("topic", topic);
    app.put("student", student);
    app.put("link", "http://www.google.com");
    app.put("degree", degree);
    app.put("status", "WAITING_APPROVAL");

    Response response = IntegrationTestSuite.authorizedRequest(CLIENT.target(String.format("http://localhost:%d/api/applications/7", DROPWIZARD.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), CLIENT).put(Entity.json(app.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 200);
    assertEquals(response.readEntity(TopicApplication.class).getOfficialAssignment(), "New");
  }

  @Test(dependsOnMethods = "updateApplication")
  public void deleteApplication() {
    Response response = IntegrationTestSuite.authorizedRequest(CLIENT.target(String.format("http://localhost:%d/api/applications/7", DROPWIZARD.getLocalPort())).request(), CLIENT)
      .delete();

    assertNotNull(response);
    assertEquals(response.getStatus(), 204);
    assertEquals(fetchApplications().size(), TopicApplicationDAOTest.COUNT);
  }

  @Test(dependsOnMethods = { "deleteApplication", "fetchApplication" })
  public void declineApplications() {
    int[] ids = {3,8};
    JSONArray apps = new JSONArray();

    for (int id : ids) {
      JSONObject app = new JSONObject();
      app.put("id", id);
      apps.add(app);
    }

    Response response = IntegrationTestSuite.authorizedRequest(CLIENT.target(String.format("http://localhost:%d/api/applications/massDecline", DROPWIZARD.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), CLIENT).post(Entity.json(apps.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 200);
    for (int id : ids) {
      TopicApplication app = fetchApplication(id);
      assertEquals(app.getStatus(), ApplicationStatus.DECLINED);
    }
  }
  /*
   * Task tests
   */

  @Test(dependsOnGroups = "login", groups = "listTasks")
  public void listTasks() {
    List<Task> list = fetchTasks();

    assertNotNull(list);
    assertEquals(list.size(), 3);
  }

}
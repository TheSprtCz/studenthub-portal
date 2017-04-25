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
import cz.studenthub.core.Task;
import cz.studenthub.core.TopicApplication;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;
import net.minidev.json.JSONObject;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TopicApplicationResourceTest {
  public static final DropwizardAppRule<StudentHubConfiguration> DROPWIZARD = IntegrationTestSuite.DROPWIZARD;

  private static Client client = new JerseyClientBuilder(DROPWIZARD.getEnvironment()).build("TopicApplicationTest");
  
  private List<TopicApplication> fetchApplications() {
    return IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/applications", DROPWIZARD.getLocalPort()))
      .request(), client).get(new GenericType<List<TopicApplication>>(){});
  }

  public static List<Task> fetchTasks() {
    return IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/applications/1/tasks", DROPWIZARD.getLocalPort()))
      .request(), client).get(new GenericType<List<Task>>(){});
  }

  @Test
  public void listApplications() {
    List<TopicApplication> list = fetchApplications();

    assertNotNull(list);
    assertEquals(7, list.size());
  }

  @Test
  public void fetchApplication() {
    TopicApplication app = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/applications/6", DROPWIZARD.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(TopicApplication.class);

    assertNotNull(app);
    assertEquals(".JSX editor plugin", app.getOfficialAssignment());
  }

  @Test
  public void createApplication() {
    JSONObject faculty = new JSONObject();
    faculty.put("id", 2);
    JSONObject topic = new JSONObject();
    topic.put("id", 2);
    JSONObject student = new JSONObject();
    student.put("id", 12);

    JSONObject app = new JSONObject();
    app.put("faculty", faculty);
    app.put("topic", topic);
    app.put("student", student);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/applications", DROPWIZARD.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).post(Entity.json(app.toJSONString()));

    assertNotNull(response);
    assertEquals(201, response.getStatus());
    assertEquals(8, fetchApplications().size());
  }

  @Test
  public void updateApplication() {
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

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/applications/5", DROPWIZARD.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).put(Entity.json(app.toJSONString()));

    assertNotNull(response);
    assertEquals(200, response.getStatus());
    assertEquals("New", response.readEntity(TopicApplication.class).getOfficialAssignment());
  }

  @Test
  public void deleteApplication() {
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/applications/8", DROPWIZARD.getLocalPort())).request(), client)
      .delete();

    assertNotNull(response);
    assertEquals(204, response.getStatus());
    assertEquals(7, fetchApplications().size());
  }

  /*
   * Task tests
   */

  @Test
  public void listTasks() {
    List<Task> list = fetchTasks();

    assertNotNull(list);
    assertEquals(3, list.size());
  }

}
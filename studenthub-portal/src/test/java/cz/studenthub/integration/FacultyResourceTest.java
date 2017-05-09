package cz.studenthub.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import cz.studenthub.IntegrationTestSuite;
import cz.studenthub.StudentHubConfiguration;
import cz.studenthub.core.Faculty;
import cz.studenthub.core.User;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.DropwizardTestSupport;
import net.minidev.json.JSONObject;

public class FacultyResourceTest {
  public static DropwizardTestSupport<StudentHubConfiguration> DROPWIZARD;

  private static Client client;

  @BeforeClass
  public void setup() {
      DROPWIZARD = IntegrationTestSuite.DROPWIZARD;
      client = new JerseyClientBuilder(DROPWIZARD.getEnvironment()).build("FacultyTest");
  }

  private List<Faculty> fetchFaculties() {
    return client.target(String.format("http://localhost:%d/api/faculties", DROPWIZARD.getLocalPort()))
      .request().get(new GenericType<List<Faculty>>(){});
  }

  @Test(dependsOnGroups = "migrate")
  public void listFaculties() {
    List<Faculty> list = fetchFaculties();

    assertNotNull(list);
    assertEquals(list.size(), 13);
  }

  @Test(dependsOnGroups = "login")
  public void fetchFaculty() {
    Faculty fac = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/faculties/1", DROPWIZARD.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(Faculty.class);

    assertNotNull(fac);
    assertEquals(fac.getName(), "Faculty of Informatics");
  }

  @Test(dependsOnGroups = "login", dependsOnMethods = "listFaculties")
  public void createFaculty() {
    JSONObject university = new JSONObject();
    university.put("id", 5);

    JSONObject faculty = new JSONObject();
    faculty.put("name", "New Faculty");
    faculty.put("university", university);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/faculties", DROPWIZARD.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).post(Entity.json(faculty.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 201);
    assertEquals(fetchFaculties().size(), 14);
  }

  @Test(dependsOnMethods = "createFaculty")
  public void updateFaculty() {
    JSONObject university = new JSONObject();
    university.put("id", 5);

    JSONObject faculty = new JSONObject();
    faculty.put("name", "New");
    faculty.put("university", university);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/faculties/14", DROPWIZARD.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).put(Entity.json(faculty.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 200);
    assertEquals(response.readEntity(Faculty.class).getName(), "New");
  }

  @Test(dependsOnMethods = "updateFaculty")
  public void deleteFaculty() {
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/faculties/14", DROPWIZARD.getLocalPort())).request(), client)
      .delete();

    assertNotNull(response);
    assertEquals(response.getStatus(), 204);
    assertEquals(fetchFaculties().size(), 13);
  }

  @Test(dependsOnGroups = "login")
  public void getStudents() {
    List<User> users = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/faculties/1/students", DROPWIZARD.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(new GenericType<List<User>>(){});

    assertNotNull(users);
    assertEquals(users.size(), 2);
  }

  @Test(dependsOnGroups = "login")
  public void getSupervisors() {
    List<User> users = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/faculties/2/supervisors", DROPWIZARD.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(new GenericType<List<User>>(){});

    assertNotNull(users);
    assertEquals(users.size(), 2);
  }

}
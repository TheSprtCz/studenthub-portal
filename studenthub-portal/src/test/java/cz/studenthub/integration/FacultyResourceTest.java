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
import cz.studenthub.core.Faculty;
import cz.studenthub.core.User;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;
import net.minidev.json.JSONObject;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FacultyResourceTest {
  public static final DropwizardAppRule<StudentHubConfiguration> DROPWIZARD = IntegrationTestSuite.DROPWIZARD;

  private static Client client = new JerseyClientBuilder(DROPWIZARD.getEnvironment()).build("FacultyTest");

  private List<Faculty> fetchFaculties() {
    return client.target(String.format("http://localhost:%d/api/faculties", DROPWIZARD.getLocalPort()))
      .request().get(new GenericType<List<Faculty>>(){});
  }

  @Test
  public void listFaculties() {
    List<Faculty> list = fetchFaculties();

    assertNotNull(list);
    assertEquals(13, list.size());
  }

  @Test
  public void fetchFaculty() {
    Faculty fac = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/faculties/1", DROPWIZARD.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(Faculty.class);

    assertNotNull(fac);
    assertEquals("Faculty of Informatics", fac.getName());
  }

  @Test
  public void createFaculty() {
    JSONObject university = new JSONObject();
    university.put("id", 5);

    JSONObject faculty = new JSONObject();
    faculty.put("name", "New Faculty");
    faculty.put("university", university);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/faculties", DROPWIZARD.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).post(Entity.json(faculty.toJSONString()));

    assertNotNull(response);
    assertEquals(201, response.getStatus());
    assertEquals(14, fetchFaculties().size());
  }

  @Test
  public void updateFaculty() {
    JSONObject university = new JSONObject();
    university.put("id", 5);

    JSONObject faculty = new JSONObject();
    faculty.put("name", "New");
    faculty.put("university", university);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/faculties/5", DROPWIZARD.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).put(Entity.json(faculty.toJSONString()));

    assertNotNull(response);
    assertEquals(200, response.getStatus());
    assertEquals("New", response.readEntity(Faculty.class).getName());
  }

  @Test
  public void deleteFaculty() {
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/faculties/14", DROPWIZARD.getLocalPort())).request(), client)
      .delete();

    assertNotNull(response);
    assertEquals(204, response.getStatus());
    assertEquals(13, fetchFaculties().size());
  }

  @Test
  public void getStudents() {
    List<User> users = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/faculties/1/students", DROPWIZARD.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(new GenericType<List<User>>(){});

    assertNotNull(users);
    assertEquals(2, users.size());
  }

  @Test
  public void getSupervisors() {
    List<User> users = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/faculties/2/supervisors", DROPWIZARD.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(new GenericType<List<User>>(){});

    assertNotNull(users);
    assertEquals(2, users.size());
  }

}
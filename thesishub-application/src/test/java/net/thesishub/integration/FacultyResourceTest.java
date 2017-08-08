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
import net.thesishub.core.Faculty;
import net.thesishub.core.Project;
import net.thesishub.core.User;
import net.thesishub.db.FacultyDAOTest;

public class FacultyResourceTest {
  private DropwizardTestSupport<ThesisHubConfiguration> dropwizard;
  private Client client;

  @BeforeClass
  public void setup() {
      dropwizard = IntegrationTestSuite.DROPWIZARD;
      client = IntegrationTestSuite.BUILDER.build("FacultyTest");
  }

  private List<Faculty> fetchFaculties() {
    return client.target(String.format("http://localhost:%d/api/faculties", dropwizard.getLocalPort()))
      .request().get(new GenericType<List<Faculty>>(){});
  }

  @Test(dependsOnGroups = "migrate")
  public void listFaculties() {
    List<Faculty> list = fetchFaculties();

    assertNotNull(list);
    assertEquals(list.size(), FacultyDAOTest.COUNT);
  }

  @Test(dependsOnGroups = "login")
  public void fetchFaculty() {
    Faculty fac = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/faculties/1", dropwizard.getLocalPort()))
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

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/faculties", dropwizard.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).post(Entity.json(faculty.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 201);
    assertEquals(fetchFaculties().size(), FacultyDAOTest.COUNT + 1);
  }

  @Test(dependsOnMethods = "createFaculty")
  public void updateFaculty() {
    JSONObject university = new JSONObject();
    university.put("id", 5);

    JSONObject faculty = new JSONObject();
    faculty.put("name", "New");
    faculty.put("university", university);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/faculties/14", dropwizard.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).put(Entity.json(faculty.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 200);
    assertEquals(response.readEntity(Faculty.class).getName(), "New");
  }

  @Test(dependsOnMethods = "updateFaculty")
  public void deleteFaculty() {
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/faculties/14", dropwizard.getLocalPort())).request(), client)
      .delete();

    assertNotNull(response);
    assertEquals(response.getStatus(), 204);
    assertEquals(fetchFaculties().size(), FacultyDAOTest.COUNT);
  }

  @Test(dependsOnGroups = "login")
  public void getStudents() {
    List<User> users = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/faculties/1/students", dropwizard.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(new GenericType<List<User>>(){});

    assertNotNull(users);
    assertEquals(users.size(), 3);
  }

  @Test(dependsOnGroups = "login")
  public void getSupervisors() {
    List<User> users = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/faculties/2/supervisors", dropwizard.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(new GenericType<List<User>>(){});

    assertNotNull(users);
    assertEquals(users.size(), 2);
  }

  @Test(dependsOnGroups = "login")
  public void fetchProjects() {
    List<Project> projects = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/faculties/2/projects", dropwizard.getLocalPort())).request(), client)
        .get(new GenericType<List<Project>>(){}); 

    assertNotNull(projects);
    assertEquals(projects.size(), 2);
  }
}
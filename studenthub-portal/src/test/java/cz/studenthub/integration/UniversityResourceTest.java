package cz.studenthub.integration;

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

import cz.studenthub.IntegrationTestSuite;
import cz.studenthub.StudentHubConfiguration;
import cz.studenthub.core.Faculty;
import cz.studenthub.core.University;
import io.dropwizard.testing.DropwizardTestSupport;
import net.minidev.json.JSONObject;

public class UniversityResourceTest {
  private DropwizardTestSupport<StudentHubConfiguration> dropwizard;
  private Client client;

  @BeforeClass
  public void setup() {
      dropwizard = IntegrationTestSuite.DROPWIZARD;
      client = IntegrationTestSuite.BUILDER.build("UniversityTest");
  }
  
  private List<University> fetchUniversities() {
    return client.target(String.format("http://localhost:%d/api/universities", dropwizard.getLocalPort()))
      .request().get(new GenericType<List<University>>(){});
  }

  @Test(dependsOnGroups = "migrate")
  public void listUniversities() {
    List<University> list = fetchUniversities();

    assertNotNull(list);
    assertEquals(list.size(), 5);
  }

  @Test(dependsOnGroups = "login")
  public void fetchUniversity() {
    University uni = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/universities/1", dropwizard.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(University.class);

    assertNotNull(uni);
    assertEquals(uni.getName(), "Masaryk University");
  }

  @Test(dependsOnMethods = "listUniversities", dependsOnGroups = "login")
  public void createUniversity() {
    JSONObject university = new JSONObject();
    university.put("name", "Unknown");
    university.put("url", "past.me");
    university.put("city", "Brno");

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/universities", dropwizard.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).post(Entity.json(university.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 201);
    assertEquals(fetchUniversities().size(), 6);
  }

  @Test(dependsOnMethods = "createUniversity")
  public void updateUniversity() {
    JSONObject university = new JSONObject();
    university.put("name", "New");

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/universities/5", dropwizard.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).put(Entity.json(university.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 200);
    assertEquals(response.readEntity(University.class).getName(), "New");
  }

  @Test(dependsOnMethods = "updateUniversity")
  public void deleteUniversity() {
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/universities/6", dropwizard.getLocalPort())).request(), client)
      .delete();

    assertNotNull(response);
    assertEquals(response.getStatus(), 204);
    assertEquals(fetchUniversities().size(), 5);
  }

  @Test(dependsOnGroups = "login")
  public void fetchFaculties() {
    List<Faculty> faculties = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/universities/1/faculties", dropwizard.getLocalPort())).request(), client)
        .get(new GenericType<List<Faculty>>(){}); 

    assertNotNull(faculties);
    assertEquals(faculties.size(), 4);
  }
}
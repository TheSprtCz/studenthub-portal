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
import cz.studenthub.core.University;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;
import net.minidev.json.JSONObject;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UniversityResourceTest {
  public static final DropwizardAppRule<StudentHubConfiguration> DROPWIZARD = IntegrationTestSuite.DROPWIZARD;

  private static Client client = new JerseyClientBuilder(DROPWIZARD.getEnvironment()).build("UniversityTest");
  
  private List<University> fetchUniversities() {
    return client.target(String.format("http://localhost:%d/api/universities", DROPWIZARD.getLocalPort()))
      .request().get(new GenericType<List<University>>(){});
  }

  @Test
  public void listUniversities() {
    List<University> list = fetchUniversities();

    assertNotNull(list);
    assertEquals(5, list.size());
  }

  @Test
  public void fetchUniversity() {
    University uni = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/universities/1", DROPWIZARD.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(University.class);

    assertNotNull(uni);
    assertEquals("Masaryk University", uni.getName());
  }

  @Test
  public void createUniversity() {
    JSONObject university = new JSONObject();
    university.put("name", "Unknown");
    university.put("url", "past.me");
    university.put("city", "Brno");

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/universities", DROPWIZARD.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).post(Entity.json(university.toJSONString()));

    assertNotNull(response);
    assertEquals(201, response.getStatus());
    assertEquals(6, fetchUniversities().size());
  }

  @Test
  public void updateUniversity() {
    JSONObject university = new JSONObject();
    university.put("name", "New");

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/universities/5", DROPWIZARD.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).put(Entity.json(university.toJSONString()));

    assertNotNull(response);
    assertEquals(200, response.getStatus());
    assertEquals("New", response.readEntity(University.class).getName());
  }

  @Test
  public void deleteUniversity() {
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/universities/6", DROPWIZARD.getLocalPort())).request(), client)
      .delete();

    assertNotNull(response);
    assertEquals(204, response.getStatus());
    assertEquals(5, fetchUniversities().size());
  }

  @Test
  public void fetchFaculties() {
    List<Faculty> faculties = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/universities/1/faculties", DROPWIZARD.getLocalPort())).request(), client)
        .get(new GenericType<List<Faculty>>(){}); 

    assertNotNull(faculties);
    assertEquals(4, faculties.size());
  }
}
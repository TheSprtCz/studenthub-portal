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

import cz.studenthub.StudentHubConfiguration;
import cz.studenthub.IntegrationTestSuite;
import cz.studenthub.core.Company;
import cz.studenthub.core.Topic;
import cz.studenthub.core.User;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.DropwizardTestSupport;
import net.minidev.json.JSONObject;

public class CompanyResourceTest {
  public static DropwizardTestSupport<StudentHubConfiguration> DROPWIZARD;

  private static Client client;

  @BeforeClass
  public void setup() {
      DROPWIZARD = IntegrationTestSuite.DROPWIZARD;
      client = new JerseyClientBuilder(DROPWIZARD.getEnvironment()).build("CompanyTest");
  }

  private List<Company> fetchFaculties() {
    return client.target(String.format("http://localhost:%d/api/companies/", DROPWIZARD.getLocalPort()))
      .request().get(new GenericType<List<Company>>(){});
  }

  @Test(dependsOnGroups = "migrate")
  public void listCompanies() {
    List<Company> list = fetchFaculties();

    assertNotNull(list);
    assertEquals(list.size(), 8);
  }

  @Test(dependsOnGroups = "login")
  public void fetchCompany() {
    Company company = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/companies/5", DROPWIZARD.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(Company.class);

    assertNotNull(company);
    assertEquals(company.getName(), "Company Five");
  }

  @Test(dependsOnMethods = "listCompanies")
  public void createCompany() {
    JSONObject company = new JSONObject();
    company.put("name", "New Company");

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/companies", DROPWIZARD.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).post(Entity.json(company.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 201);
    assertEquals(fetchFaculties().size(), 9);
  }

  @Test(dependsOnMethods = "createCompany")
  public void updateCompany() {
    JSONObject company = new JSONObject();
    company.put("url", "past.me");
    company.put("name", "New Company");

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/companies/9", DROPWIZARD.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).put(Entity.json(company.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 200);
    assertEquals(response.readEntity(Company.class).getUrl(), "past.me");
  }

  @Test(dependsOnMethods = "updateCompany")
  public void deleteCompany() {
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/companies/9", DROPWIZARD.getLocalPort())).request(), client)
      .delete();
    List<Company> list = fetchFaculties();

    assertNotNull(response);
    assertEquals(response.getStatus(), 204);
    assertEquals(list.size(), 8);
  }

  @Test(dependsOnGroups = "login")
  public void getLeaders() {
    List<User> users = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/companies/5/leaders", DROPWIZARD.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(new GenericType<List<User>>(){});

    assertNotNull(users);
    assertEquals(1, users.size());
  }

  @Test(dependsOnGroups = "login")
  public void getTopics() {
    List<Topic> topics = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/companies/5/topics", DROPWIZARD.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(new GenericType<List<Topic>>(){});

    assertNotNull(topics);
    assertEquals(2, topics.size());
  }

}
package cz.studenthub.integration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

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
import cz.studenthub.core.CompanyPlan;
import cz.studenthub.core.Project;
import cz.studenthub.core.Topic;
import cz.studenthub.core.User;
import cz.studenthub.db.CompanyDAOTest;
import io.dropwizard.testing.DropwizardTestSupport;
import net.minidev.json.JSONObject;

public class CompanyResourceTest {
  private DropwizardTestSupport<StudentHubConfiguration> dropwizard;
  private Client client;

  @BeforeClass
  public void setup() {
      dropwizard = IntegrationTestSuite.DROPWIZARD;
      client = IntegrationTestSuite.BUILDER.build("CompanyTest");
  }

  private List<Company> fetchCompanies() {
    return client.target(String.format("http://localhost:%d/api/companies/", dropwizard.getLocalPort()))
      .request().get(new GenericType<List<Company>>(){});
  }

  @Test(dependsOnGroups = "migrate")
  public void listCompanies() {
    List<Company> list = fetchCompanies();

    assertNotNull(list);
    assertEquals(list.size(), CompanyDAOTest.COUNT);
  }

  @Test(dependsOnGroups = "login")
  public void fetchCompany() {
    Company company = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/companies/5", dropwizard.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(Company.class);

    assertNotNull(company);
    assertEquals(company.getName(), "Company Five");
  }

  @Test(dependsOnMethods = "listCompanies", dependsOnGroups = "login")
  public void createCompany() {
    JSONObject plan = new JSONObject();
    plan.put("name", "TIER_1");

    JSONObject company = new JSONObject();
    company.put("name", "New Company");
    company.put("plan", plan);
    
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/companies", dropwizard.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).post(Entity.json(company.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 201);
    assertNull(response.readEntity(Company.class).getPlan());
    assertEquals(fetchCompanies().size(), CompanyDAOTest.COUNT + 1);
  }

  @Test(dependsOnMethods = "createCompany")
  public void updateCompany() {
    JSONObject plan = new JSONObject();
    plan.put("name", "TIER_1");

    JSONObject company = new JSONObject();
    company.put("url", "http://www.past.me");
    company.put("name", "New Company");
    company.put("plan", plan);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/companies/9", dropwizard.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).put(Entity.json(company.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 200);
    assertEquals(response.readEntity(Company.class).getUrl(), "http://www.past.me");
  }

  @Test(dependsOnMethods = "updateCompany")
  public void deleteCompany() {
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/companies/9", dropwizard.getLocalPort())).request(), client)
      .delete();
    List<Company> list = fetchCompanies();

    assertNotNull(response);
    assertEquals(response.getStatus(), 204);
    assertEquals(list.size(), CompanyDAOTest.COUNT);
  }

  @Test(dependsOnGroups = "login")
  public void getLeaders() {
    List<User> users = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/companies/5/leaders", dropwizard.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(new GenericType<List<User>>(){});

    assertNotNull(users);
    assertEquals(1, users.size());
  }

  @Test(dependsOnGroups = "migrate")
  public void getTopics() {
    List<Topic> topics = client.target(String.format("http://localhost:%d/api/companies/5/topics", dropwizard.getLocalPort()))
        .request(MediaType.APPLICATION_JSON).get(new GenericType<List<Topic>>(){});

    assertNotNull(topics);
    assertEquals(2, topics.size());
  }

  @Test(dependsOnGroups = "login")
  public void getPlan() {
    CompanyPlan plan = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/companies/5/plan", dropwizard.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(CompanyPlan.class);

    assertNotNull(plan);
    assertEquals("TIER_3", plan.getName());
  }

  @Test(dependsOnGroups = "migrate")
  public void fetchProjects() {
    List<Project> projects = client.target(String.format("http://localhost:%d/api/companies/2/projects", dropwizard.getLocalPort())).request()
        .get(new GenericType<List<Project>>(){}); 

    assertNotNull(projects);
    assertEquals(projects.size(), 1);
    assertEquals((long) projects.get(0).getId(), 1);
  }

}
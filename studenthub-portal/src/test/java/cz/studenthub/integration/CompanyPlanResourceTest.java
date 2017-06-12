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
import cz.studenthub.core.CompanyPlan;
import io.dropwizard.testing.DropwizardTestSupport;
import net.minidev.json.JSONObject;

public class CompanyPlanResourceTest {
  private DropwizardTestSupport<StudentHubConfiguration> dropwizard;
  private Client client;

  @BeforeClass
  public void setup() {
      dropwizard = IntegrationTestSuite.DROPWIZARD;
      client = IntegrationTestSuite.BUILDER.build("CompanyPlanTest");
  }

  private List<CompanyPlan> fetchCompanyPlans() {
    return IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/plans/", dropwizard.getLocalPort()))
      .request(), client).get(new GenericType<List<CompanyPlan>>(){});
  }

  @Test(dependsOnGroups = "login")
  public void listCompanyPlans() {
    List<CompanyPlan> list = fetchCompanyPlans();

    assertNotNull(list);
    assertEquals(list.size(), 4);
  }

  @Test(dependsOnGroups = "login")
  public void fetchCompanyPlan() {
    CompanyPlan company = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/plans/TIER_1", dropwizard.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(CompanyPlan.class);

    assertNotNull(company);
    assertEquals(company.getMaxTopics(), 3);
  }

  @Test(dependsOnMethods = "listCompanyPlans")
  public void createCompanyPlan() {
    JSONObject plan = new JSONObject();
    plan.put("name", "TIER_5");
    plan.put("maxTopics", 20);
    
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/plans", dropwizard.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).post(Entity.json(plan.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 201);
    assertEquals(fetchCompanyPlans().size(), 5);
  }

  @Test(dependsOnMethods = "createCompanyPlan")
  public void updateCompanyPlan() {
    JSONObject plan = new JSONObject();
    plan.put("name", "TIER_5");
    plan.put("maxTopics", 10);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/plans/TIER_5", dropwizard.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).put(Entity.json(plan.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 200);
    assertEquals(response.readEntity(CompanyPlan.class).getMaxTopics(), 10);
  }

  @Test(dependsOnMethods = "updateCompanyPlan")
  public void deleteCompanyPlan() {
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/plans/TIER_5", dropwizard.getLocalPort())).request(), client)
      .delete();
    List<CompanyPlan> list = fetchCompanyPlans();

    assertNotNull(response);
    assertEquals(response.getStatus(), 204);
    assertEquals(list.size(), 4);
  }

}
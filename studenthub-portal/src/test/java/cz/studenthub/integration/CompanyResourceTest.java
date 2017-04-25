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
import cz.studenthub.core.Company;
import cz.studenthub.core.Topic;
import cz.studenthub.core.User;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;
import net.minidev.json.JSONObject;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CompanyResourceTest {
  public static final DropwizardAppRule<StudentHubConfiguration> DROPWIZARD = IntegrationTestSuite.DROPWIZARD;

  private static Client client = new JerseyClientBuilder(DROPWIZARD.getEnvironment()).build("CompanyTest");

  private List<Company> fetchFaculties() {
    return client.target(String.format("http://localhost:%d/api/companies/", DROPWIZARD.getLocalPort()))
      .request().get(new GenericType<List<Company>>(){});
  }

  @Test
  public void listCompanies() {
    List<Company> list = fetchFaculties();

    assertNotNull(list);
    assertEquals(8, list.size());
  }

  @Test
  public void fetchCompany() {
    Company company = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/companies/5", DROPWIZARD.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(Company.class);

    assertNotNull(company);
    assertEquals("Company Five", company.getName());
  }

  @Test
  public void createCompany() {
    JSONObject company = new JSONObject();
    company.put("name", "New Company");

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/companies", DROPWIZARD.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).post(Entity.json(company.toJSONString()));

    assertNotNull(response);
    assertEquals(201, response.getStatus());
    assertEquals(9, fetchFaculties().size());
  }

  @Test
  public void updateCompany() {
    JSONObject company = new JSONObject();
    company.put("url", "past.me");
    company.put("name", "New Company");

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/companies/5", DROPWIZARD.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).put(Entity.json(company.toJSONString()));

    assertNotNull(response);
    assertEquals(200, response.getStatus());
    assertEquals("past.me", response.readEntity(Company.class).getUrl());
  }

  @Test
  public void deleteCompany() {
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/companies/9", DROPWIZARD.getLocalPort())).request(), client)
      .delete();
    List<Company> list = fetchFaculties();

    assertNotNull(response);
    assertEquals(204, response.getStatus());
    assertEquals(8, list.size());
  }

  @Test
  public void getLeaders() {
    List<User> users = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/companies/5/leaders", DROPWIZARD.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(new GenericType<List<User>>(){});

    assertNotNull(users);
    assertEquals(1, users.size());
  }

  @Test
  public void getTopics() {
    List<Topic> topics = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/companies/5/topics", DROPWIZARD.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(new GenericType<List<Topic>>(){});

    assertNotNull(topics);
    assertEquals(2, topics.size());
  }

}
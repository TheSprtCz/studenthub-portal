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
import cz.studenthub.core.Country;
import cz.studenthub.db.CountryDAOTest;
import io.dropwizard.testing.DropwizardTestSupport;
import net.minidev.json.JSONObject;

public class CountryResourceTest {
  private DropwizardTestSupport<StudentHubConfiguration> dropwizard;
  private Client client;

  @BeforeClass
  public void setup() {
      dropwizard = IntegrationTestSuite.DROPWIZARD;
      client = IntegrationTestSuite.BUILDER.build("CompanyPlanTest");
  }

  private List<Country> fetchCountries() {
    return client.target(String.format("http://localhost:%d/api/countries", dropwizard.getLocalPort()))
      .request().get(new GenericType<List<Country>>(){});
  }

  @Test(dependsOnGroups = "login")
  public void listCountries() {
    List<Country> list = fetchCountries();

    assertNotNull(list);
    assertEquals(list.size(), CountryDAOTest.COUNT);
  }

  @Test(dependsOnGroups = "login")
  public void fetchCountry() {
    Country country = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/countries/SK", dropwizard.getLocalPort()))
        .request(MediaType.APPLICATION_JSON), client).get(Country.class);

    assertNotNull(country);
    assertEquals(country.getName(), "Slovakia");
  }

  @Test(dependsOnMethods = "listCountries")
  public void createCountry() {
    JSONObject country = new JSONObject();
    country.put("name", "Estonia");
    country.put("tag", "ES");
    
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/countries", dropwizard.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).post(Entity.json(country.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 201);
    assertEquals(fetchCountries().size(), CountryDAOTest.COUNT + 1);
  }

  @Test(dependsOnMethods = "createCountry")
  public void updateCountry() {
    JSONObject country = new JSONObject();
    country.put("name", "Poland");
    country.put("tag", "PL");

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/countries/HU", dropwizard.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).put(Entity.json(country.toJSONString()));

    Country updated = response.readEntity(Country.class);

    assertNotNull(response);
    assertNotNull(updated);
    assertEquals(response.getStatus(), 200);
    assertEquals(updated.getTag(), "HU");
    assertEquals(updated.getName(), "Poland");
  }

  @Test(dependsOnMethods = "updateCountry")
  public void deleteCountry() {
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/countries/HU", dropwizard.getLocalPort())).request(), client)
      .delete();
    List<Country> list = fetchCountries();

    assertNotNull(response);
    assertEquals(response.getStatus(), 204);
    assertEquals(list.size(), CountryDAOTest.COUNT);
  }

}
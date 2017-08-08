package net.thesishub.integration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.dropwizard.testing.DropwizardTestSupport;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.thesishub.IntegrationTestSuite;
import net.thesishub.ThesisHubConfiguration;
import net.thesishub.core.Faculty;
import net.thesishub.core.Project;
import net.thesishub.core.Topic;
import net.thesishub.core.TopicApplication;
import net.thesishub.core.User;
import net.thesishub.db.ProjectDAOTest;

public class ProjectResourceTest {
  public DropwizardTestSupport<ThesisHubConfiguration> dropwizard;
  private Client client;

  @BeforeClass
  public void setup() {
      dropwizard = IntegrationTestSuite.DROPWIZARD;
      client = IntegrationTestSuite.BUILDER.build("ProjectTest");
  }
  
  private List<Project> fetchProjects() {
    return client.target(String.format("http://localhost:%d/api/projects", dropwizard.getLocalPort()))
      .request().get(new GenericType<List<Project>>(){});
  }

  @Test(dependsOnGroups = "migrate")
  public void listProjects() {
    List<Project> list = fetchProjects();

    assertNotNull(list);
    assertEquals(list.size(), ProjectDAOTest.COUNT);
  }

  @Test(dependsOnGroups = "migrate", groups = "fetchProject")
  public void fetchProject() {
    Project topic = client.target(String.format("http://localhost:%d/api/projects/2", dropwizard.getLocalPort()))
        .request(MediaType.APPLICATION_JSON)
        .get(Project.class);

    assertNotNull(topic);
    assertEquals(topic.getName(), "Industry things");
  }

  @Test(dependsOnMethods = "listProjects", dependsOnGroups = "login")
  public void createProject() {
    JSONObject creator = new JSONObject();
    creator.put("id", 20);

    JSONArray creators = new JSONArray();
    creators.add(creator);

    JSONObject project = new JSONObject();
    project.put("name", "New Project");
    project.put("creators", creators);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/projects", dropwizard.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).post(Entity.json(project.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 201);
    assertEquals(fetchProjects().size(), ProjectDAOTest.COUNT + 1);
    assertEquals((long) response.readEntity(Project.class).getId(), 3);
  }

  @Test(dependsOnMethods = "createProject")
  public void updateProject() {
    JSONObject creator = new JSONObject();
    creator.put("id", 20);

    JSONArray creators = new JSONArray();
    creators.add(creator);

    JSONObject project = new JSONObject();
    project.put("name", "Projects 101");
    project.put("creators", creators);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/projects/3", dropwizard.getLocalPort()))
      .request(MediaType.APPLICATION_JSON), client).put(Entity.json(project.toJSONString()));

    assertNotNull(response);
    assertEquals(response.getStatus(), 200);
    assertEquals(response.readEntity(Project.class).getName(), "Projects 101");
  }

  @Test(dependsOnMethods = "updateProject")
  public void deleteProject() {
    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/projects/3", dropwizard.getLocalPort()))
      .request(), client).delete();

    assertNotNull(response);
    assertEquals(response.getStatus(), 204);
    assertEquals(fetchProjects().size(), ProjectDAOTest.COUNT);
  }

  @Test(dependsOnGroups = {"login", "fetchTopic", "fetchProject"})
  public void assignTopic() {
    Topic topic = client.target(String.format("http://localhost:%d/api/topics/3", dropwizard.getLocalPort())).request().get(Topic.class);

    Response response = IntegrationTestSuite.authorizedRequest(client.target(String.format("http://localhost:%d/api/projects/2/assignTopic/3", dropwizard.getLocalPort())).request(), client)
        .put(Entity.json(""));

    Project project = client.target(String.format("http://localhost:%d/api/projects/2", dropwizard.getLocalPort())).request(MediaType.APPLICATION_JSON).get(Project.class);

    assertNotNull(response);
    assertEquals(response.getStatus(), 200);
    assertTrue(project.getTopics().contains(topic));
  }

  @Test(dependsOnGroups = "migrate")
  public void fetchApplications() {
    List<TopicApplication> apps = client.target(String.format("http://localhost:%d/api/projects/1/applications", dropwizard.getLocalPort()))
        .request().get(new GenericType<List<TopicApplication>>(){});

    assertNotNull(apps);
    assertEquals(apps.size(), 3);
  }

  @Test(dependsOnGroups = "migrate")
  public void fetchFaculties() {
    List<Faculty> faculties = client.target(String.format("http://localhost:%d/api/projects/1/faculties", dropwizard.getLocalPort()))
        .request().get(new GenericType<List<Faculty>>(){});

    assertNotNull(faculties);
    assertEquals(faculties.size(), 4);
  }

  @Test(dependsOnGroups = "migrate")
  public void fetchTopics() {
    List<Topic> topics = client.target(String.format("http://localhost:%d/api/projects/1/topics", dropwizard.getLocalPort()))
        .request().get(new GenericType<List<Topic>>(){});

    assertNotNull(topics);
    assertEquals(topics.size(), 2);
  }

  @Test(dependsOnGroups = "migrate")
  public void fetchCreators() {
    List<User> creators = client.target(String.format("http://localhost:%d/api/projects/2/creators", dropwizard.getLocalPort()))
        .request().get(new GenericType<List<User>>(){});

    assertNotNull(creators);
    assertEquals(creators.size(), 1);
  }

}
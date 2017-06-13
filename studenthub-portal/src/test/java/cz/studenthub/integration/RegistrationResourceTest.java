package cz.studenthub.integration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.hibernate.Session;
import org.hibernate.context.internal.ManagedSessionContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

import cz.studenthub.IntegrationTestSuite;
import cz.studenthub.StudentHubConfiguration;
import cz.studenthub.core.Activation;
import cz.studenthub.core.User;
import cz.studenthub.db.ActivationDAO;
import cz.studenthub.db.UserDAO;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.DropwizardTestSupport;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class RegistrationResourceTest {
  private static final int MAIL_TIMEOUT = 20000;

  private DropwizardTestSupport<StudentHubConfiguration> dropwizard;
  private GreenMail greenMail = new GreenMail(ServerSetupTest.SMTP);
  private ActivationDAO actDAO;
  private UserDAO userDAO;
  private Session session;

  private Client client;

  @BeforeClass
  public void setup() {
      dropwizard = IntegrationTestSuite.DROPWIZARD;
      client = new JerseyClientBuilder(dropwizard.getEnvironment()).build("RegistrationTest");
      greenMail.start();
      session = IntegrationTestSuite.getSessionFactory().openSession();
      ManagedSessionContext.bind(session);

      actDAO = new ActivationDAO(IntegrationTestSuite.getSessionFactory());
      userDAO = new UserDAO(IntegrationTestSuite.getSessionFactory());
  }

  @Test(groups = "testMail")
  public void testMail() {
    GreenMailUtil.sendTextEmailTest("to@localhost.com", "from@localhost.com", "subject", "body");
    MimeMessage[] emails = greenMail.getReceivedMessages();
    assertEquals(1, emails.length);
  }

  @AfterClass
  public void tearDown() {
    greenMail.stop();
    session.close();
    ManagedSessionContext.unbind(IntegrationTestSuite.getSessionFactory());
  }

  @AfterMethod
  public void reset() {
    greenMail.reset();
  }

  // This test both signUp and activate endpoints
  @Test(dependsOnGroups = {"login", "testMail"}, groups = "signUp")
  public void SignUpProcessTest() throws MessagingException, IOException {
    JSONObject faculty = new JSONObject();
    faculty.put("id", 5);

    JSONArray roles = new JSONArray();
    roles.add("STUDENT");

    JSONObject user = new JSONObject();
    user.put("name", "New One");
    user.put("username", "one");
    user.put("email", "exact@gmail.com");    
    user.put("faculty", faculty);
    user.put("roles", roles);

    // test response code
    Response response = client.target(String.format("http://localhost:%d/api/account/signUp", dropwizard.getLocalPort()))
        .request(MediaType.APPLICATION_JSON).post(Entity.json(user.toJSONString()));
    assertEquals(response.getStatus(), 201);

    // Test that activation was created
    User usr = response.readEntity(User.class);
    Activation act = actDAO.findByUser(usr);

    // Test that exactly one email arrived
    assertTrue(greenMail.waitForIncomingEmail(MAIL_TIMEOUT, 1));
    Message[] messages = greenMail.getReceivedMessages();
    assertEquals(messages.length, 1);

    // Test that email has correct subject
    Message msg = messages[0];
    assertEquals(msg.getSubject(), "Password Setup");

    // Test that activation email contains activationCode
    String content = GreenMailUtil.getBody(msg);
    assertTrue(content.contains(act.getActivationCode()));

    // Test that activation request will be successful
    String password = "se17oip#8íéý";
    MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
    formData.add("password", password);

    Response actResponse = client.target(String.format("http://localhost:%d/api/account/activate", dropwizard.getLocalPort()))
        .queryParam("secret", act.getActivationCode())
        .queryParam("id", usr.getId())
        .request().post(Entity.form(formData));
    assertEquals(actResponse.getStatus(), 200);

    // Test that confirmation email was received
    assertTrue(greenMail.waitForIncomingEmail(MAIL_TIMEOUT, 1));
    messages = greenMail.getReceivedMessages();
    assertEquals(messages.length, 2);
    assertEquals(messages[1].getSubject(), "User Activation");
    // Ensure that password is not present in body
    assertFalse(GreenMailUtil.getBody(messages[1]).contains(password));

    // Test that activation will be removed from DB
    assertNull(actDAO.findByUser(usr));

    // Test that we can get login token with new password
    Response loginResponse = IntegrationTestSuite.authorizationRequest(client, "exact@gmail.com", password);
    assertNotNull(loginResponse);
    assertNotNull(loginResponse.getCookies().get("sh-token"));
  }

  @Test(dependsOnGroups = {"migrate","testMail"})
  public void resendActivationTest() throws MessagingException {
    MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
    formData.add("email", "rep3@example.com");

    // Test response status
    Response response = client.target(String.format("http://localhost:%d/api/account/resendActivation", dropwizard.getLocalPort()))
        .request().post(Entity.form(formData));
    assertEquals(response.getStatus(), 200);

    // Test received email
    assertTrue(greenMail.waitForIncomingEmail(MAIL_TIMEOUT, 1));
    MimeMessage[] messages = greenMail.getReceivedMessages();
    assertEquals(messages.length, 1);
    assertEquals(messages[0].getSubject(), "Password Setup");
  }

  @Test(dependsOnGroups = {"migrate","testMail"})
  public void resetPasswordTest() throws MessagingException {
    MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
    formData.add("email", "rep2@example.com");

    User user = userDAO.findByEmail("rep2@example.com");
    // Test response status
    Response response = client.target(String.format("http://localhost:%d/api/account/resetPassword", dropwizard.getLocalPort()))
        .request().post(Entity.form(formData));
    assertEquals(response.getStatus(), 200);

    // Test if activation was created
    Activation act = actDAO.findByUser(user);
    assertNotNull(act);

    // Test if correct mail was sent
    assertTrue(greenMail.waitForIncomingEmail(MAIL_TIMEOUT, 1));
    MimeMessage[] messages = greenMail.getReceivedMessages();
    assertEquals(messages.length, 1);
    assertEquals(messages[0].getSubject(), "Password Setup");
  }

  @Test(dependsOnGroups = {"migrate","testMail"})
  public void changePasswordTest() throws MessagingException {
    String password = "xPo#|€@5897";
    JSONObject updateBean = new JSONObject();
    updateBean.put("oldPwd", "test");
    updateBean.put("newPwd", password);

    // Test response status
    Response response = IntegrationTestSuite.oneTimeAuthorizedRequest(
        client.target(String.format("http://localhost:%d/api/account/16/password", dropwizard.getLocalPort())).request(MediaType.APPLICATION_JSON),
        client, "rep1@example.com", "test").put(Entity.json(updateBean.toJSONString()));
    assertEquals(response.getStatus(), 200);

    // Test if correct mail was sent
    assertTrue(greenMail.waitForIncomingEmail(MAIL_TIMEOUT, 1));
    MimeMessage[] messages = greenMail.getReceivedMessages();
    assertEquals(messages.length, 1);
    assertEquals(messages[0].getSubject(), "Password Updated");
    // Ensure that password is not present in body
    assertFalse(GreenMailUtil.getBody(messages[0]).contains(password));

    // Test that we can get login token
    Response loginResponse = IntegrationTestSuite.authorizationRequest(client, "rep1@example.com", password);
    assertNotNull(loginResponse);
    assertNotNull(loginResponse.getCookies().get("sh-token"));
  }  
  
}

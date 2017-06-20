package net.thesishub.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.Map;

import org.junit.Test;

public class UrlUtilTest {

  private static final UrlUtil util = new UrlUtil("http://localhost:8080");

  @Test
  public void testBasicResource() {
   assertEquals("http://localhost:8080/login", util.getUri("login").toString());
  }

  @Test
  public void testResourceAction() {
   assertEquals("http://localhost:8080/account/activate", util.getUri("account", "activate").toString());
  }

  @Test
  public void testLoginUrl() {
    Map<String, Object> arguments = util.createLoginUrl();
    assertNotNull(arguments);
    assertEquals("http://localhost:8080/profile", arguments.get("loginUrl"));
  }
}

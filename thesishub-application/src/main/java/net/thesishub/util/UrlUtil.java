package net.thesishub.util;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;

public class UrlUtil {

  private String domain;

  public UrlUtil(String domain) {
    this.domain = domain;
  }

  // Gets URL of resource e.g localhost:8080/api/profile
  public UriBuilder getUri(String controller) {
    return UriBuilder.fromPath(domain).path(controller);
  }

  // Gets URL of resource and specified action e.g localhost:8080/api/account/confirmReset
  public UriBuilder getUri(String controller, String action) {
    return getUri(controller).path(action);
  }

  // Adds loginUrl to existing argument map
  public Map<String, Object> addLoginUrl(Map<String, Object> map) {
    map.put("loginUrl", getUri("profile").toString());
    return map;
  }

  // Creates new argument map and insert loginUrl there
  public Map<String, Object> createLoginUrl() {
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("loginUrl", getUri("profile").toString());
    return map;
  }
}

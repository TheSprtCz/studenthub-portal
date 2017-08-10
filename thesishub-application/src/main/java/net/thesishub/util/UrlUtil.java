/*******************************************************************************
 * Copyright (C) 2017  Stefan Bunciak
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.thesishub.util;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;

/**
 * Utility class for constructing Uris based on domain Thesis Hub is running on.
 * 
 * @author phala
 * @since 1.1
 */
public class UrlUtil {

  private String domain;

  public UrlUtil(String domain) {
    this.domain = domain;
  }

  /**
   * Constructs Uri based on Thesis Hub domain and giver path segments.
   * 
   * @param paths array of path segments
   * @return UriBuilder
   */
  public UriBuilder getUriBuilder(String... paths) {
    return UriBuilder.fromPath(domain).segment(paths);
  }

  /**
   * Constructs Uri based on Thesis Hub domain and giver path segments.
   * 
   * @param paths array of path segments
   * @return String representation of constructed Uri
   */
  public String getUri(String... paths) {
    return UriBuilder.fromPath(domain).segment(paths).toString();
  }

  /**
   * Adds loginUrl to existing argument map
   * 
   * @param map with properties
   * @return supplied and modified Map
   */
  public Map<String, Object> addLoginUrl(Map<String, Object> map) {
    map.put("loginUrl", getUriBuilder("profile").toString());
    return map;
  }
 
  /**
   * Creates new argument map and inserts loginUrl
   * 
   * @return Map with loginUrl
   */
  public Map<String, Object> createLoginUrl() {
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("loginUrl", getUri("profile"));
    return map;
  }
}

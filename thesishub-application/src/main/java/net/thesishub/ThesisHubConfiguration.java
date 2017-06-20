/*******************************************************************************
 *     Copyright (C) 2017  Stefan Bunciak
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.thesishub;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import net.thesishub.util.SmtpConfig;

/**
 * Configuration for entire DW application
 * 
 * @author sbunciak
 * @since 1.0
 */
public class ThesisHubConfiguration extends Configuration {

  @Valid
  @NotNull
  private DataSourceFactory database = new DataSourceFactory();

  @Valid
  @NotNull
  private SmtpConfig smtp = new SmtpConfig();

  private String jwtSecret;

  @NotNull
  @URL
  private String domain;

  private boolean enableBasicAuth = false;
  
  @JsonProperty("database")
  public DataSourceFactory getDataSourceFactory() {
    return database;
  }

  @JsonProperty("database")
  public void setDataSourceFactory(DataSourceFactory factory) {
    this.database = factory;
  }

  @JsonProperty("smtp")
  public SmtpConfig getSmtpConfig() {
    return smtp;
  }

  @JsonProperty("smtp")
  public void setSmtpConfig(SmtpConfig smtp) {
    this.smtp = smtp;
  }
  
  @JsonProperty("jwtSecret")
  public void setJwtSecret(String jwtSecret) {
    this.jwtSecret = jwtSecret;
  }
  
  @JsonProperty("jwtSecret")
  public String getJwtSecret() {
    return jwtSecret;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  @JsonProperty("enableBasicAuth")
  public boolean isBasicAuthEnabled() {
    return enableBasicAuth;
  }

  @JsonProperty("enableBasicAuth")
  public void setEnabledBasicAuth(boolean enableBasicAuth) {
    this.enableBasicAuth = enableBasicAuth;
  }

  
}

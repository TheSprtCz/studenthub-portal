/*******************************************************************************
 *     Copyright (C) 2016  Stefan Bunciak
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
package cz.studenthub.core;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Entity
@Table(name = "Companies")
@NamedQueries({ @NamedQuery(name = "Company.findAll", query = "SELECT company FROM Company company") })
public class Company {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotEmpty
  private String name;

  @URL
  private String url;
  private String city;

  @Enumerated(EnumType.STRING)
  private Country country;

  @URL
  private String logoUrl;

  @Enumerated(EnumType.STRING)
  private CompanySize size;

  @ManyToOne
  @NotNull
  @JsonProperty(access = Access.WRITE_ONLY)
  private CompanyPlan plan;

  public Company() {
  }

  public Company(String name, String url, String city, Country country, String logoUrl, CompanySize size,
      CompanyPlan plan) {
    this.name = name;
    this.url = url;
    this.city = city;
    this.country = country;
    this.logoUrl = logoUrl;
    this.size = size;
    this.plan = plan;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

  public String getLogoUrl() {
    return logoUrl;
  }

  public void setLogoUrl(String logoUrl) {
    this.logoUrl = logoUrl;
  }

  public CompanySize getSize() {
    return size;
  }

  public void setSize(CompanySize size) {
    this.size = size;
  }

  public CompanyPlan getPlan() {
    return plan;
  }

  public void setPlan(CompanyPlan plan) {
    this.plan = plan;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, url, city, size, plan);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }

    return Integer.compare(this.hashCode(), obj.hashCode()) == 0;
  }

  @Override
  public String toString() {
    return String.format("Company[id=%s, name=%s, url=%s, city=%s]", id, name, url, city);
  }
}

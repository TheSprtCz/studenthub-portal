package net.thesishub.core;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "Plans")
@NamedQueries({ @NamedQuery(name = "CompanyPlan.findAll", query = "SELECT companyPlan FROM CompanyPlan companyPlan") })
public class CompanyPlan {

  @Id
  @Column(name = "name", unique = true)
  @NotEmpty
  private String name;

  private String description;

  @NotNull
  @Min(0)
  private int maxTopics;

  public CompanyPlan() {
  }

  public CompanyPlan(String name, int maxTopics) {
    this.name = name;
    this.maxTopics = maxTopics;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getMaxTopics() {
    return maxTopics;
  }

  public void setMaxTopics(int maxTopics) {
    this.maxTopics = maxTopics;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, maxTopics);
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
    return String.format("CompanyPlan[name=%s, maxTopics=%d, description=%s]", name, maxTopics, description);
  }
}

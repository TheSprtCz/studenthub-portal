package net.thesishub.core;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "Degrees")
@NamedQueries({ @NamedQuery(name = "TopicDegree.findAll", query = "SELECT degree FROM TopicDegree degree") })
public class TopicDegree {

  @Id
  @Column(name = "name", unique = true)
  @NotEmpty
  private String name;

  private String description;

  public TopicDegree() {
  }

  public TopicDegree(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description);
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
    return String.format("TopicDegree[name=%s, description=%s]", name, description);
  }
}

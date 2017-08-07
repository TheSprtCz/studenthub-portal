package cz.studenthub.core;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "Countries")
@NamedQueries({ @NamedQuery(name = "Country.findAll", query = "SELECT country FROM Country country") })
public class Country {

  @Id
  @Column(name = "tag", unique = true)
  @NotEmpty
  private String tag;

  @NotEmpty
  private String name;

  public Country() {
  }

  public Country(String tag, String name) {
    this.tag = tag;
    this.name = name;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String name) {
    this.tag = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public int hashCode() {
    return Objects.hash(tag, name);
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
    return String.format("Country[tag=%s, name=%s]", tag, name);
  }
}

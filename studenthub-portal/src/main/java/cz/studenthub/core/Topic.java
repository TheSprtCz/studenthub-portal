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
import java.util.Set;

import javax.annotation.Nullable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "Topics")
@NamedQueries({ @NamedQuery(name = "Topic.findAll", query = "SELECT topic FROM Topic topic"),
  @NamedQuery(name = "Topic.findByCreator", query = "SELECT topic FROM Topic topic WHERE topic.creator = :creator"),
  @NamedQuery(name = "Topic.findBySupervisor", query = "SELECT topic FROM Topic topic join topic.academicSupervisors supervisor WHERE supervisor = :supervisor"),
  @NamedQuery(name = "Topic.findByTag", query = "SELECT topic FROM Topic topic join topic.tags tag WHERE tag = :tag AND enabled = TRUE"),
  @NamedQuery(name = "Topic.findByCompany", query = "SELECT topic FROM Topic topic WHERE topic.creator.company = :company AND enabled = TRUE") })
public class Topic {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotEmpty
  private String title;
  private String shortAbstract;
  private String description;
  private boolean enabled = true;

  /*
   * TODO: This is a possible data integrity issue (e.g. student can be a
   * leader)
   */
  @JsonIgnore
  @ManyToOne
  @NotNull
  private User creator;

  @JsonIgnore
  @Nullable
  @ManyToMany
  private Set<User> academicSupervisors;

  @ElementCollection(fetch = FetchType.EAGER)
  private Set<String> tags;

  /*
   * TODO: topic degrees should be stored/configurable in DB or conf file
   */
  @ElementCollection(fetch = FetchType.EAGER)
  private Set<TopicDegree> degrees;

  public Topic() {
  }

  public Topic(String title, String shortAbstract, String description, User creator, Set<User> academicSupervisors,
      Set<String> tags, Set<TopicDegree> degrees) {
    this.title = title;
    this.shortAbstract = shortAbstract;
    this.description = description;
    this.creator = creator;
    this.academicSupervisors = academicSupervisors;
    this.tags = tags;
    this.degrees = degrees;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getShortAbstract() {
    return shortAbstract;
  }

  public void setShortAbstract(String shortAbstract) {
    this.shortAbstract = shortAbstract;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @JsonIgnore
  public User getCreator() {
    return creator;
  }

  @JsonProperty
  public void setCreator(User leader) {
    this.creator = leader;
  }

  @JsonIgnore
  public Set<User> getAcademicSupervisors() {
    return academicSupervisors;
  }

  @JsonProperty
  public void setAcademicSupervisor(Set<User> academicSupervisors) {
    this.academicSupervisors = academicSupervisors;
  }

  public void addAcademicSupervisor(User academicSupervisor) {
    this.academicSupervisors.add(academicSupervisor);
  }
  
  public Set<String> getTags() {
    return tags;
  }

  public void setTags(Set<String> tags) {
    this.tags = tags;
  }

  public Set<TopicDegree> getDegrees() {
    return degrees;
  }

  public void setDegrees(Set<TopicDegree> degrees) {
    this.degrees = degrees;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  } 

  @Override
  public int hashCode() {
    return Objects.hash(id, title, shortAbstract, description);
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
    return String.format("Topic[id=%s, title=%s]", id, title);
  }
}

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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import cz.studenthub.core.User;

@Entity
@Table(name = "Topics")
@NamedQueries({ @NamedQuery(name = "Topic.findAll", query = "SELECT t FROM Topic t") })
public class Topic {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotEmpty
  private String title;
  private String shortAbstract;
  private String description;

  /*
   * TODO: This is a possible data integrity issue (e.g. student can be a
   * leader)
   */
  @ManyToOne
  private User techLeader;

  @Nullable
  @ManyToOne
  private User academicSupervisor;

  @ElementCollection(fetch = FetchType.EAGER)
  private Set<String> tags;

  /*
   * TODO: topic degrees should be stored/configurable in DB or conf file
   */
  @ElementCollection(fetch = FetchType.EAGER)
  private Set<TopicDegree> degrees;

  public Topic() {
  }

  public Topic(String title, String shortAbstract, String description, User techLeader, User academicSupervisor,
      Set<String> tags, Set<TopicDegree> degrees) {
    this.title = title;
    this.shortAbstract = shortAbstract;
    this.description = description;
    this.techLeader = techLeader;
    this.academicSupervisor = academicSupervisor;
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

  public User getTechLeader() {
    return techLeader;
  }

  public void setTechLeader(User leader) {
    this.techLeader = leader;
  }

  public User getAcademicSupervisor() {
    return academicSupervisor;
  }

  public void setAcademicSupervisor(User academicSupervisor) {
    this.academicSupervisor = academicSupervisor;
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

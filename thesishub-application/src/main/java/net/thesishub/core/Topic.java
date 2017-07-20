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
package net.thesishub.core;

import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import net.thesishub.validators.annotations.Role;
import net.thesishub.validators.groups.CreateUpdateChecks;

@Entity
@Table(name = "Topics")
@NamedQueries({ @NamedQuery(name = "Topic.findAll", query = "SELECT topic FROM Topic topic"),
  @NamedQuery(name = "Topic.findByCreator", query = "SELECT topic FROM Topic topic WHERE topic.creator = :creator"),
  @NamedQuery(name = "Topic.findBySupervisor", query = "SELECT topic FROM Topic topic join topic.academicSupervisors supervisor WHERE supervisor = :supervisor"),
  @NamedQuery(name = "Topic.findByTag", query = "SELECT topic FROM Topic topic join topic.tags tag WHERE tag = :tag AND enabled = TRUE"),
  @NamedQuery(name = "Topic.findByCompany", query = "SELECT topic FROM Topic topic WHERE topic.creator.company = :company AND enabled = TRUE"),
  @NamedQuery(name = "Topic.countByCompany", query = "SELECT COUNT(topic) FROM Topic topic WHERE topic.creator.company = :company AND enabled = TRUE") })
public class Topic extends GenericEntity<Long> {

  @NotEmpty
  private String title;
  private String secondaryTitle;
  private String shortAbstract;
  private String description;
  private String secondaryDescription;
  private boolean enabled = true;

  @NotNull
  @ManyToOne(fetch = FetchType.EAGER)
  @Role(role = UserRole.TECH_LEADER, groups = CreateUpdateChecks.class)
  private User creator;

  @Nullable
  @ManyToMany
  @Role(role = UserRole.AC_SUPERVISOR, groups = CreateUpdateChecks.class)
  private Set<User> academicSupervisors;

  @ElementCollection(fetch = FetchType.EAGER)
  private Set<String> tags;

  @ManyToMany
  @NotEmpty
  private Set<TopicDegree> degrees;

  public Topic() {
  }

  public Topic(String title, String secondaryTitle, String shortAbstract, String description, String secondaryDescription, User creator, Set<User> academicSupervisors,
      Set<String> tags, Set<TopicDegree> degrees) {
    this.title = title;
    this.secondaryTitle = secondaryTitle;
    this.shortAbstract = shortAbstract;
    this.description = description;
    this.secondaryDescription = secondaryDescription;
    this.creator = creator;
    this.academicSupervisors = academicSupervisors;
    this.tags = tags;
    this.degrees = degrees;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSecondaryTitle() {
    return secondaryTitle;
  }

  public void setSecondaryTitle(String secondaryTitle) {
    this.secondaryTitle = secondaryTitle;
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

  public String getSecondaryDescription() {
    return secondaryDescription;
  }

  public void setSecondaryDescription(String secondaryDescription) {
    this.secondaryDescription = secondaryDescription;
  }

  public User getCreator() {
    return creator;
  }

  public void setCreator(User leader) {
    this.creator = leader;
  }

  public Set<User> getAcademicSupervisors() {
    return academicSupervisors;
  }

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
  public String toString() {
    return String.format("Topic[id=%s, title=%s]", id, title);
  }

}

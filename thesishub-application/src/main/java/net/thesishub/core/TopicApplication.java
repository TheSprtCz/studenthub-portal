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

import java.util.Date;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.thesishub.validators.annotations.Role;
import net.thesishub.validators.groups.CreateUpdateChecks;
import net.thesishub.validators.groups.NotNullChecks;
import net.thesishub.validators.groups.ValidationMethodChecks;
import io.dropwizard.validation.ValidationMethod;

@Entity
@Table(name = "TopicApplications")
@NamedQueries({ @NamedQuery(name = "TopicApplication.findAll", query = "SELECT app FROM TopicApplication app"),
  @NamedQuery(name = "TopicApplication.findByFaculty", query = "SELECT app FROM TopicApplication app WHERE app.faculty = :faculty"),
  @NamedQuery(name = "TopicApplication.findByTopic", query = "SELECT app FROM TopicApplication app WHERE app.topic = :topic"),
  @NamedQuery(name = "TopicApplication.findByTopics", query = "SELECT app FROM TopicApplication app WHERE app.topic IN :topics"),
  @NamedQuery(name = "TopicApplication.findByStudent", query = "SELECT app FROM TopicApplication app WHERE app.student = :student"),
  @NamedQuery(name = "TopicApplication.findByLeader", query = "SELECT app FROM TopicApplication app WHERE app.techLeader = :leader"),
  @NamedQuery(name = "TopicApplication.findBySupervisor", query = "SELECT app FROM TopicApplication app WHERE app.academicSupervisor = :supervisor") })
@GroupSequence({ NotNullChecks.class, ValidationMethodChecks.class, TopicApplication.class })
public class TopicApplication extends GenericEntity<Long> {

  @NotNull
  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Topic topic;

  private String officialAssignment;

  @Nullable
  @Enumerated(EnumType.STRING)
  private TopicGrade grade;

  @ManyToOne
  @NotNull
  private TopicDegree degree;

  private Date thesisFinish;
  private Date thesisStart;

  @Enumerated(EnumType.STRING)
  @NotNull(groups = NotNullChecks.class)
  private ApplicationStatus status;

  @NotNull
  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Faculty faculty;

  @ManyToOne
  @Role(role = UserRole.TECH_LEADER, groups = CreateUpdateChecks.class)
  private User techLeader;

  @NotNull
  @ManyToOne
  @Role(role = UserRole.STUDENT, groups = CreateUpdateChecks.class)
  private User student;

  @ManyToOne
  @Role(role = UserRole.AC_SUPERVISOR, groups = CreateUpdateChecks.class)
  private User academicSupervisor;

  @URL
  private String link;

  public TopicApplication() {
  }

  public TopicApplication(Topic topic, String officialAssignment, TopicGrade grade, TopicDegree degree,
      Date thesisFinish, Date thesisStart, Faculty faculty, User techLeader, User student, User academicSupervisor,
      String link, ApplicationStatus status) {
    this.topic = topic;
    this.officialAssignment = officialAssignment;
    this.grade = grade;
    this.degree = degree;
    this.thesisFinish = thesisFinish;
    this.thesisStart = thesisStart;
    this.faculty = faculty;
    this.techLeader = techLeader;
    this.student = student;
    this.academicSupervisor = academicSupervisor;
    this.link = link;
    this.status = status;
  }

  public Topic getTopic() {
    return topic;
  }

  public void setTopic(Topic topic) {
    this.topic = topic;
  }

  public TopicGrade getGrade() {
    return grade;
  }

  public void setGrade(TopicGrade grade) {
    this.grade = grade;
  }

  public TopicDegree getDegree() {
    return degree;
  }

  public void setDegree(TopicDegree degree) {
    this.degree = degree;
  }

  public Date getThesisFinish() {
    return thesisFinish;
  }

  public void setThesisFinish(Date thesisFinish) {
    this.thesisFinish = thesisFinish;
  }

  public Date getThesisStart() {
    return thesisStart;
  }

  public void setThesisStart(Date thesisStart) {
    this.thesisStart = thesisStart;
  }

  public ApplicationStatus getStatus() {
    return status;
  }

  public void setStatus(ApplicationStatus status) {
    this.status = status;
  }

  public Faculty getFaculty() {
    return faculty;
  }

  public void setFaculty(Faculty faculty) {
    this.faculty = faculty;
  }

  public User getTechLeader() {
    return techLeader;
  }

  public void setTechLeader(User techLeader) {
    this.techLeader = techLeader;
  }

  public User getStudent() {
    return student;
  }

  public void setStudent(User student) {
    this.student = student;
  }

  public User getAcademicSupervisor() {
    return academicSupervisor;
  }

  public void setAcademicSupervisor(User academicSupervisor) {
    this.academicSupervisor = academicSupervisor;
  }

  public String getOfficialAssignment() {
    return officialAssignment;
  }

  public void setOfficialAssignment(String officialAssignment) {
    this.officialAssignment = officialAssignment;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, thesisFinish, grade, degree);
  }

  @Override
  public String toString() {
    return String.format("TopicApplication[id=%s]", id);
  }

  // Validation methods
  @ValidationMethod(message = "If thesis is IN_PROGRESS it has to have thesisStart and can't have grade", groups = ValidationMethodChecks.class)
  @JsonIgnore
  public boolean isInProgress() {
    if (status == ApplicationStatus.IN_PROGRESS)
      return thesisStart != null && grade == null;

    return true;
  }

  @ValidationMethod(message = "If thesis is FINISHED it has to have thesisStart, thesisFinish and grade", groups = ValidationMethodChecks.class)
  @JsonIgnore
  public boolean isFinished() {
    if (status == ApplicationStatus.FINISHED)
      return thesisStart != null && thesisFinish != null && grade != null;

    return true;
  }

  @ValidationMethod(message = "If thesis is WAITING it can't have grade", groups = ValidationMethodChecks.class)
  @JsonIgnore
  public boolean isWaitingOrDeclined() {
    if (status == ApplicationStatus.WAITING_APPROVAL || status == ApplicationStatus.DECLINED)
      return grade == null;

    return true;
  }

  @ValidationMethod(message = "If thesis is FAILED it must have thesisStart", groups = ValidationMethodChecks.class)
  @JsonIgnore
  public boolean isFailed() {
    if (status == ApplicationStatus.FAILED)
      return thesisStart != null;

    return true;
  }
}
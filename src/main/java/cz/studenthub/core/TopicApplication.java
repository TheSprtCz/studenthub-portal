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

import java.util.Date;
import java.util.Objects;

import javax.annotation.Nullable;
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

import cz.studenthub.core.User;

@Entity
@Table(name = "TopicApplications")
@NamedQueries({ @NamedQuery(name = "TopicApplication.findAll", query = "SELECT ta FROM TopicApplication ta") })
public class TopicApplication {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Topic topic;

  @Nullable
  @Enumerated(EnumType.STRING)
  private TopicGrade grade;

  @Enumerated(EnumType.STRING)
  private TopicDegree degree;

  private Date thesisFinish;

  @NotNull
  @ManyToOne
  private Faculty faculty;

  @ManyToOne
  private User techLeader;

  @ManyToOne
  private User student;

  @ManyToOne
  private User academicSupervisor;

  public TopicApplication() {
  }

  public TopicApplication(Topic topic, TopicGrade grade, TopicDegree degree, Date thesisFinish, Faculty faculty,
      User techLeader, User student, User academicSupervisor) {
    this.topic = topic;
    this.grade = grade;
    this.degree = degree;
    this.thesisFinish = thesisFinish;
    this.faculty = faculty;
    this.techLeader = techLeader;
    this.student = student;
    this.academicSupervisor = academicSupervisor;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  @Override
  public int hashCode() {
    return Objects.hash(id, thesisFinish, grade, degree);
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
    return String.format("TopicApplication[id=%s]", id);
  }
}

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
package net.thesishub.core;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Represents single todo item in Topic Applications
 * 
 * @author sbunciak
 *
 */
@Entity
@Table(name = "Tasks")
@NamedQueries({
    @NamedQuery(name = "Task.findByApplication", query = "SELECT task FROM Task task WHERE task.application = :application ORDER BY task.id") })
public class Task extends GenericEntity<Long> {

  @NotEmpty
  private String title;

  private boolean completed;

  private Date deadline;

  @ManyToOne
  @NotNull
  @OnDelete(action = OnDeleteAction.CASCADE)
  private TopicApplication application;

  public Task() {
  }

  public Task(String title, boolean completed, Date deadline, TopicApplication application) {
    this.title = title;
    this.completed = completed;
    this.deadline = deadline;
    this.application = application;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  public Date getDeadline() {
    return deadline;
  }

  public void setDeadline(Date deadline) {
    this.deadline = deadline;
  }

  public TopicApplication getApplication() {
    return application;
  }

  public void setApplication(TopicApplication application) {
    this.application = application;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, completed, deadline);
  }

  @Override
  public String toString() {
    return String.format("Task[id=%s, title=%s]", id, title);
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
}

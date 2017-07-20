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

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "Faculties")
@NamedQueries({ @NamedQuery(name = "Faculty.findAll", query = "SELECT faculty FROM Faculty faculty"),
  @NamedQuery(name = "Faculty.findByUniversity", query = "SELECT faculty FROM Faculty faculty WHERE faculty.university = :university") })
public class Faculty extends GenericEntity<Long> {

  @NotEmpty
  private String name;

  @ManyToOne
  @NotNull
  @OnDelete(action = OnDeleteAction.CASCADE)
  private University university;

  public Faculty() {
  }

  public Faculty(String name, University university) {
    this.name = name;
    this.university = university;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public University getUniversity() {
    return university;
  }

  public void setUniversity(University university) {
    this.university = university;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
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
    return String.format("Faculty[id=%s, name=%s]", id, name);
  }
}

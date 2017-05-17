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

import java.security.Principal;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "Users")
@NamedQueries({ @NamedQuery(name = "User.findAll", query = "SELECT user FROM User user"),
    @NamedQuery(name = "User.findByRole", query = "SELECT user FROM User user join user.roles role WHERE role = :role"),
    @NamedQuery(name = "User.findByCompany", query = "SELECT user FROM User user WHERE user.company = :company"),
    @NamedQuery(name = "User.findByRoleAndFaculty", query = "SELECT user FROM User user join user.roles role WHERE user.faculty = :faculty and role = :role"),
    @NamedQuery(name = "User.findByRoleAndCompany", query = "SELECT user FROM User user join user.roles role WHERE user.company = :company and role = :role"),
    @NamedQuery(name = "User.findByTag", query = "SELECT user FROM User user join user.tags tag WHERE tag = :tag"),
    @NamedQuery(name = "User.findByEmail", query = "SELECT user FROM User user WHERE user.email = :email")})
public class User implements Principal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String username;

  @JsonIgnore
  private String password;

  @Column(unique = true, nullable = false)
  private String email;
  @NotEmpty
  private String name;
  private String phone;

  @Nullable
  private Timestamp lastLogin;

  /*
   * If user is a Student or an Academic Supervisor
   */
  @Nullable
  @ManyToOne
  private Faculty faculty;

  /*
   * If user is a CompanyRep or Technical Leader
   */
  @Nullable
  @ManyToOne
  private Company company;

  @NotEmpty
  @Enumerated(EnumType.STRING)
  @ElementCollection(fetch = FetchType.EAGER)
  private Set<UserRole> roles;

  /*
   * Interests of a Student, Skills of a Leader/Supervisor
   */
  @ElementCollection(fetch = FetchType.EAGER)
  private Set<String> tags;

  public User() {
  }

  public User(String username, String password, String email, String name, String phone, Faculty faculty,
      Company company, Set<UserRole> roles, Set<String> tags) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.name = name;
    this.phone = phone;
    this.faculty = faculty;
    this.company = company;
    this.roles = roles;
    this.tags = tags;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String fullName) {
    this.name = fullName;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public Timestamp getLastLogin() {
    return lastLogin;
  }

  public void setLastLogin(Timestamp lastLogin) {
    this.lastLogin = lastLogin;
  }

  public Faculty getFaculty() {
    return faculty;
  }

  public void setFaculty(Faculty faculty) {
    this.faculty = faculty;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public Set<UserRole> getRoles() {
    return roles;
  }

  public void setRoles(Set<UserRole> roles) {
    this.roles = roles;
  }

  public Set<String> getTags() {
    return tags;
  }

  public void setTags(Set<String> tags) {
    this.tags = tags;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, email, lastLogin, phone);
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
    return String.format("User[id=%s, name=%s, email=%s]", id, name, email);
  }
}

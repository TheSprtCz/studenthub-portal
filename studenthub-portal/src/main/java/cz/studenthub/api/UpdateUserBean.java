package cz.studenthub.api;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.annotation.Nullable;

import org.hibernate.validator.constraints.NotEmpty;

import cz.studenthub.core.Company;
import cz.studenthub.core.Faculty;
import cz.studenthub.core.UserRole;

/**
 * Class used when updating a User. In order to not touch the password.
 * 
 * @author sbunciak
 *
 */
public class UpdateUserBean implements Serializable {

  private static final long serialVersionUID = 2755798347164439304L;

  @NotEmpty
  private String username;

  @NotEmpty
  private String email;

  @NotEmpty
  private String name;
  
  private String phone;

  private Timestamp lastLogin;

  @Nullable
  private Faculty faculty;

  @Nullable
  private Company company;

  @NotEmpty
  private Set<UserRole> roles;

  private Set<String> tags;

  public UpdateUserBean() {
  }
  
  public UpdateUserBean(String username, String email, String name, String phone, Timestamp lastLogin, Faculty faculty,
      Company company, Set<UserRole> roles, Set<String> tags) {
    this.username = username;
    this.email = email;
    this.name = name;
    this.phone = phone;
    this.lastLogin = lastLogin;
    this.faculty = faculty;
    this.company = company;
    this.roles = roles;
    this.tags = tags;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
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

  public void setName(String name) {
    this.name = name;
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
}

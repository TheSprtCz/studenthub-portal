package cz.studenthub.core;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "Projects")
@NamedQueries({ @NamedQuery(name = "Project.findAll", query = "SELECT project FROM Project project"),
  @NamedQuery(name = "Project.findByTopic", query = "SELECT project FROM Project project join project.topics topic WHERE topic = :topic"),
  @NamedQuery(name = "Project.findByCompany", query = "SELECT project FROM Project project join project.companies company WHERE company = :company"),
  @NamedQuery(name = "Project.findByCreator", query = "SELECT project FROM Project project join project.creators creator WHERE creator = :creator"),
  @NamedQuery(name = "Project.findByFaculty", query = "SELECT project FROM Project project join project.faculties faculty WHERE faculty = :faculty") })
public class Project {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotEmpty
  private String name;
  private String description;

  @NotEmpty
  @ManyToMany
  private Set<User> creators;

  @ManyToMany
  private Set<Company> companies;

  @ManyToMany
  private Set<Faculty> faculties;

  @ManyToMany
  private Set<Topic> topics;

  public Project() {  
  }

  public Project(String name, String description, Set<User> creators, Set<Company> companies,
      Set<Faculty> faculties, Set<Topic> topics) {
    this.name = name;
    this.description = description;
    this.creators = creators;
    this.companies = companies;
    this.faculties = faculties;
    this.topics = topics;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Set<User> getCreators() {
    return creators;
  }

  public void setCreators(Set<User> creators) {
    this.creators = creators;
  }

  public Set<Company> getCompanies() {
    return companies;
  }

  public void setCompanies(Set<Company> companies) {
    this.companies = companies;
  }

  public Set<Faculty> getFaculties() {
    return faculties;
  }

  public void setFaculties(Set<Faculty> faculties) {
    this.faculties = faculties;
  }

  public Set<Topic> getTopics() {
    return topics;
  }

  public void setTopics(Set<Topic> topics) {
    this.topics = topics;
  }
}

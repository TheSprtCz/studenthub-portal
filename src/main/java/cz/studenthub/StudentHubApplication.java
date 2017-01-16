package cz.studenthub;

import cz.studenthub.core.Company;
import cz.studenthub.core.Faculty;
import cz.studenthub.core.Topic;
import cz.studenthub.core.TopicApplication;
import cz.studenthub.core.University;
import cz.studenthub.core.User;
import cz.studenthub.db.CompanyDAO;
import cz.studenthub.db.FacultyDAO;
import cz.studenthub.db.TopicApplicationDAO;
import cz.studenthub.db.TopicDAO;
import cz.studenthub.db.UniversityDAO;
import cz.studenthub.db.UserDAO;
import cz.studenthub.resources.CompanyResource;
import cz.studenthub.resources.FacultyResource;
import cz.studenthub.resources.TopicApplicationResource;
import cz.studenthub.resources.TopicResource;
import cz.studenthub.resources.UniversityResource;
import cz.studenthub.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Dropwizard application entry point.
 * 
 * @author sbunciak
 * @since 1.0
 */
public class StudentHubApplication extends Application<StudentHubConfiguration> {

  /*
   * Bundle initialization
   */
  private final HibernateBundle<StudentHubConfiguration> hibernate = new HibernateBundle<StudentHubConfiguration>(
      // list of entities
      User.class, Topic.class, TopicApplication.class, Company.class, University.class, Faculty.class) {

    @Override
    public DataSourceFactory getDataSourceFactory(StudentHubConfiguration configuration) {
      return configuration.getDataSourceFactory();
    }
  };

  /*
   * Main method
   */
  public static void main(final String[] args) throws Exception {
    new StudentHubApplication().run(args);
  }

  @Override
  public String getName() {
    return "Student Hub";
  }

  @Override
  public void initialize(final Bootstrap<StudentHubConfiguration> bootstrap) {
    bootstrap.addBundle(hibernate);
  }

  @Override
  public void run(final StudentHubConfiguration configuration, final Environment environment) {
    // initialize DAOs
    final CompanyDAO companyDao = new CompanyDAO(hibernate.getSessionFactory());
    final UniversityDAO uniDao = new UniversityDAO(hibernate.getSessionFactory());
    final FacultyDAO facDao = new FacultyDAO(hibernate.getSessionFactory());
    final UserDAO userDao = new UserDAO(hibernate.getSessionFactory());
    final TopicDAO topicDao = new TopicDAO(hibernate.getSessionFactory());
    final TopicApplicationDAO taDao = new TopicApplicationDAO(hibernate.getSessionFactory());

    // register resource classes (REST Endpoints)
    environment.jersey().register(new CompanyResource(companyDao));
    environment.jersey().register(new UniversityResource(uniDao));
    environment.jersey().register(new FacultyResource(facDao));
    environment.jersey().register(new UserResource(userDao));
    environment.jersey().register(new TopicResource(topicDao));
    environment.jersey().register(new TopicApplicationResource(taDao));
  }
}

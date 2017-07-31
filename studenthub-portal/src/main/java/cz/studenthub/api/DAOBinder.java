package cz.studenthub.api;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.hibernate.SessionFactory;

import cz.studenthub.db.ActivationDAO;
import cz.studenthub.db.CompanyDAO;
import cz.studenthub.db.CompanyPlanDAO;
import cz.studenthub.db.FacultyDAO;
import cz.studenthub.db.ProjectDAO;
import cz.studenthub.db.TaskDAO;
import cz.studenthub.db.TopicApplicationDAO;
import cz.studenthub.db.TopicDAO;
import cz.studenthub.db.TopicDegreeDAO;
import cz.studenthub.db.UniversityDAO;
import cz.studenthub.db.UserDAO;

public class DAOBinder extends AbstractBinder {

  private SessionFactory factory;

  public DAOBinder(SessionFactory factory) {
    this.factory = factory;
  }

  @Override
  protected void configure() {
    bind(new TopicDAO(factory)).to(TopicDAO.class);
    bind(new CompanyDAO(factory)).to(CompanyDAO.class);
    bind(new FacultyDAO(factory)).to(FacultyDAO.class);
    bind(new UniversityDAO(factory)).to(UniversityDAO.class);
    bind(new CompanyPlanDAO(factory)).to(CompanyPlanDAO.class);
    bind(new UserDAO(factory)).to(UserDAO.class);
    bind(new TaskDAO(factory)).to(TaskDAO.class);
    bind(new TopicApplicationDAO(factory)).to(TopicApplicationDAO.class);
    bind(new ActivationDAO(factory)).to(ActivationDAO.class);
    bind(new ProjectDAO(factory)).to(ProjectDAO.class);
    bind(new TopicDegreeDAO(factory)).to(TopicDegreeDAO.class);
  }
  
}

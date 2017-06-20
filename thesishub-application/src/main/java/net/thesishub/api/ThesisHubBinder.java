package net.thesishub.api;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.hibernate.SessionFactory;

import net.thesishub.ThesisHubConfiguration;
import net.thesishub.db.ActivationDAO;
import net.thesishub.db.CompanyDAO;
import net.thesishub.db.CompanyPlanDAO;
import net.thesishub.db.CountryDAO;
import net.thesishub.db.FacultyDAO;
import net.thesishub.db.ProjectDAO;
import net.thesishub.db.TaskDAO;
import net.thesishub.db.TopicApplicationDAO;
import net.thesishub.db.TopicDAO;
import net.thesishub.db.TopicDegreeDAO;
import net.thesishub.db.UniversityDAO;
import net.thesishub.db.UserDAO;
import net.thesishub.util.MailClient;
import net.thesishub.util.UrlUtil;

public class ThesisHubBinder extends AbstractBinder {

  private SessionFactory factory;
  private ThesisHubConfiguration configuration;

  public ThesisHubBinder(SessionFactory factory, ThesisHubConfiguration config) {
    this.factory = factory;
    this.configuration = config;
  }

  @Override
  protected void configure() {
    // Bind DAOs
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
    bind(new CountryDAO(factory)).to(CountryDAO.class);

    // Bind other stuff
    bind(new MailClient(configuration.getSmtpConfig())).to(MailClient.class);
    bind(new UrlUtil(configuration.getDomain())).to(UrlUtil.class);
  }
  
}

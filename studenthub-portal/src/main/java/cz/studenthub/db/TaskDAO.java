package cz.studenthub.db;

import java.util.List;

import org.hibernate.SessionFactory;

import cz.studenthub.core.Task;
import cz.studenthub.core.TopicApplication;
import io.dropwizard.hibernate.AbstractDAO;

public class TaskDAO extends AbstractDAO<Task> {

  public TaskDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Task update(Task t) {
    currentSession().clear();
    return persist(t);
  }
  
  public Task create(Task t) {
    return persist(t);
  }
  
  public Task findById(Long id) {
    return get(id);
  }
  
  public List<Task> findByTopicApplication(TopicApplication app) {
    return list(namedQuery("Task.findByApplication").setParameter("application", app));
  }
  
  public void delete(Task t) {
    currentSession().delete(t);
  }
  
}

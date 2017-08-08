package net.thesishub.db;

import java.util.List;

import org.hibernate.SessionFactory;

import io.dropwizard.hibernate.AbstractDAO;
import net.thesishub.core.Task;
import net.thesishub.core.TopicApplication;

/**
 * Data(base) Access Object for Task objects.
 * 
 * @author phala
 * @since 1.0
 */
public class TaskDAO extends AbstractDAO<Task> {

  public TaskDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Task update(Task task) {
    currentSession().clear();
    return persist(task);
  }
  
  public Task create(Task task) {
    return persist(task);
  }
  
  public Task findById(Long id) {
    return get(id);
  }
  
  public List<Task> findByTopicApplication(TopicApplication app) {
    return list(namedQuery("Task.findByApplication").setParameter("application", app));
  }
  
  public void delete(Task task) {
    currentSession().delete(task);
  }
  
}

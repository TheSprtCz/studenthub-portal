package net.thesishub.db;

import java.util.List;

import org.hibernate.SessionFactory;

import net.thesishub.core.Task;
import net.thesishub.core.TopicApplication;

/**
 * Data(base) Access Object for Task objects.
 * 
 * @author phala
 * @since 1.0
 */
public class TaskDAO extends GenericDAO<Task, Long> {

  public TaskDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }
  
  public List<Task> findByTopicApplication(TopicApplication app) {
    return list(namedQuery("Task.findByApplication").setParameter("application", app));
  }  
}
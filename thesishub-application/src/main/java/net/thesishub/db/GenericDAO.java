package net.thesishub.db;

import java.io.Serializable;
import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ManagedSessionContext;

import io.dropwizard.hibernate.AbstractDAO;

/*
 * Generic DAO containing all basic CRUD methods and a way to simulate UnitOfWork used for multiple insertions etc.
 *  
 * @author phala
 * @since 1.1
 */
public class GenericDAO<Type, ID extends Serializable> extends AbstractDAO<Type> {

  public interface PerformWork {
    public void perform();
  }
  
  private final SessionFactory sessionFactory;

  public GenericDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
    this.sessionFactory = sessionFactory;
  }

  // CRUD methods
  public Type update(Type entity) {
    currentSession().clear();
    return persist(entity);
  }
  
  public Type create(Type entity) {
    return persist(entity);
  }

  public Type findById(ID id) {
    return get(id);
  }

  public void delete(Type entity) {
    currentSession().delete(entity);
  }

  // UnitOfWork workaround
  public void perform(PerformWork performWork) {
    final Session session = sessionFactory.openSession();
    if(ManagedSessionContext.hasBind(sessionFactory)) {
      throw new IllegalStateException("Already in a unit of work!");
    }
    try {
      configureSession(session);      
      ManagedSessionContext.bind(session);
      session.beginTransaction();
      try {
        performWork.perform();
        commitTransaction(session);
      } catch (Exception e) {
        rollbackTransaction(session);
        this.<RuntimeException>rethrow(e);
      }
    } finally {
      session.close();
      ManagedSessionContext.unbind(sessionFactory);
    }
  }


  private void configureSession(Session session) {
    session.setDefaultReadOnly(false);
    session.setCacheMode(CacheMode.NORMAL);
  }

  private void rollbackTransaction(Session session) {
    final Transaction txn = session.getTransaction();
    if (txn != null && txn.isActive()) {
      txn.rollback();
    }
  }

  private void commitTransaction(Session session) {
    final Transaction txn = session.getTransaction();
    if (txn != null && txn.isActive()) {
      txn.commit();
    }
  }

  @SuppressWarnings("unchecked")
  private <E extends Exception> void rethrow(Exception e) throws E {
    throw (E) e;
  }
}
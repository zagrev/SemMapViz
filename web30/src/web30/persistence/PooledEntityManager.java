/**
 *
 */
package web30.persistence;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;

import org.apache.log4j.Logger;

/**
 * This class extends the Entity Manager to allow it to auto-close
 */
public class PooledEntityManager implements EntityManager, Closeable
{
   /** the wrapped entity manager */
   private final EntityManager delegate;
   /** logger */
   private final Logger log = Logger.getLogger(getClass());

   /**
    * create a auto-closeable entity manager
    * 
    * @param manager
    *           the wrapped entity manager
    */
   public PooledEntityManager(final EntityManager manager)
   {
      this.delegate = manager;
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#clear()
    */
   @Override
   public void clear()
   {
      delegate.clear();
   }

   /**
    * This close implements the close from the Closeable interface, not the close from EntityManager. That means this
    * close just returns the EntityManager to the pool.
    * 
    * @see javax.persistence.EntityManager#close()
    * @see Closeable#close()
    */
   @Override
   public void close()
   {
      if (delegate == null)
      {
         throw new IllegalArgumentException();
      }
      if (delegate.getTransaction().isActive())
      {
         log.warn("ROLLING BACK TRANSACTION");
         delegate.getTransaction().rollback();
      }
      // TODO determine whether a flush is needed (would need to begin a new transaction)
      delegate.clear();
      EntityManagerPool.returnEntityManager(this);
   }

   /**
    * This close actually closes the delegate, which you should never have to do.
    * 
    * @see javax.persistence.EntityManager#close()
    */
   public void closeEntityManager()
   {
      delegate.close();
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#contains(java.lang.Object)
    */
   @Override
   public boolean contains(final Object entity)
   {
      return delegate.contains(entity);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#createEntityGraph(java.lang.Class)
    */
   @Override
   public <T> EntityGraph<T> createEntityGraph(final Class<T> rootType)
   {
      return delegate.createEntityGraph(rootType);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#createEntityGraph(java.lang.String)
    */
   @Override
   public EntityGraph<?> createEntityGraph(final String graphName)
   {
      return delegate.createEntityGraph(graphName);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#createNamedQuery(java.lang.String)
    */
   @Override
   public Query createNamedQuery(final String name)
   {
      return delegate.createNamedQuery(name);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#createNamedQuery(java.lang.String, java.lang.Class)
    */
   @Override
   public <T> TypedQuery<T> createNamedQuery(final String name, final Class<T> resultClass)
   {
      return delegate.createNamedQuery(name, resultClass);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#createNamedStoredProcedureQuery(java.lang.String)
    */
   @Override
   public StoredProcedureQuery createNamedStoredProcedureQuery(final String name)
   {
      return delegate.createNamedStoredProcedureQuery(name);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#createNativeQuery(java.lang.String)
    */
   @Override
   public Query createNativeQuery(final String arg0)
   {
      return delegate.createNativeQuery(arg0);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#createNativeQuery(java.lang.String, java.lang.Class)
    */
   @Override
   public Query createNativeQuery(final String arg0, final Class arg1)
   {
      return delegate.createNativeQuery(arg0, arg1);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#createNativeQuery(java.lang.String, java.lang.String)
    */
   @Override
   public Query createNativeQuery(final String arg0, final String arg1)
   {
      return delegate.createNativeQuery(arg0, arg1);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#createQuery(javax.persistence.criteria.CriteriaDelete)
    */
   @Override
   public Query createQuery(final CriteriaDelete deleteQuery)
   {
      return delegate.createQuery(deleteQuery);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#createQuery(javax.persistence.criteria.CriteriaQuery)
    */
   @Override
   public <T> TypedQuery<T> createQuery(final CriteriaQuery<T> createQuery)
   {
      return delegate.createQuery(createQuery);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#createQuery(javax.persistence.criteria.CriteriaUpdate)
    */
   @Override
   public Query createQuery(final CriteriaUpdate updateQuery)
   {
      return delegate.createQuery(updateQuery);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#createQuery(java.lang.String)
    */
   @Override
   public Query createQuery(final String qlString)
   {
      return delegate.createQuery(qlString);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#createQuery(java.lang.String, java.lang.Class)
    */
   @Override
   public <T> TypedQuery<T> createQuery(final String arg0, final Class<T> arg1)
   {
      return delegate.createQuery(arg0, arg1);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#createStoredProcedureQuery(java.lang.String)
    */
   @Override
   public StoredProcedureQuery createStoredProcedureQuery(final String arg0)
   {
      return delegate.createStoredProcedureQuery(arg0);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#createStoredProcedureQuery(java.lang.String, java.lang.Class[])
    */
   @Override
   public StoredProcedureQuery createStoredProcedureQuery(final String procedureName, final Class... resultClasses)
   {
      return delegate.createStoredProcedureQuery(procedureName, resultClasses);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#createStoredProcedureQuery(java.lang.String, java.lang.String[])
    */
   @Override
   public StoredProcedureQuery createStoredProcedureQuery(final String procedureName, final String... resultSetMappings)
   {
      return delegate.createStoredProcedureQuery(procedureName, resultSetMappings);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#detach(java.lang.Object)
    */
   @Override
   public void detach(final Object arg0)
   {
      delegate.detach(arg0);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#find(java.lang.Class, java.lang.Object)
    */
   @Override
   public <T> T find(final Class<T> arg0, final Object arg1)
   {
      return delegate.find(arg0, arg1);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#find(java.lang.Class, java.lang.Object, javax.persistence.LockModeType)
    */
   @Override
   public <T> T find(final Class<T> arg0, final Object arg1, final LockModeType arg2)
   {
      return delegate.find(arg0, arg1, arg2);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#find(java.lang.Class, java.lang.Object, javax.persistence.LockModeType,
    * java.util.Map)
    */
   @Override
   public <T> T find(final Class<T> arg0, final Object arg1, final LockModeType arg2, final Map<String, Object> arg3)
   {
      return delegate.find(arg0, arg1, arg2, arg3);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#find(java.lang.Class, java.lang.Object, java.util.Map)
    */
   @Override
   public <T> T find(final Class<T> arg0, final Object arg1, final Map<String, Object> arg2)
   {
      return delegate.find(arg0, arg1, arg2);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#flush()
    */
   @Override
   public void flush()
   {
      delegate.flush();
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#getCriteriaBuilder()
    */
   @Override
   public CriteriaBuilder getCriteriaBuilder()
   {
      return delegate.getCriteriaBuilder();
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#getDelegate()
    */
   @Override
   public Object getDelegate()
   {
      return delegate.getDelegate();
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#getEntityGraph(java.lang.String)
    */
   @Override
   public EntityGraph<?> getEntityGraph(final String arg0)
   {
      return delegate.getEntityGraph(arg0);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#getEntityGraphs(java.lang.Class)
    */
   @Override
   public <T> List<EntityGraph<? super T>> getEntityGraphs(final Class<T> arg0)
   {
      return delegate.getEntityGraphs(arg0);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#getEntityManagerFactory()
    */
   @Override
   public EntityManagerFactory getEntityManagerFactory()
   {
      return delegate.getEntityManagerFactory();
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#getFlushMode()
    */
   @Override
   public FlushModeType getFlushMode()
   {
      return delegate.getFlushMode();
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#getLockMode(java.lang.Object)
    */
   @Override
   public LockModeType getLockMode(final Object arg0)
   {
      return delegate.getLockMode(arg0);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#getMetamodel()
    */
   @Override
   public Metamodel getMetamodel()
   {
      return delegate.getMetamodel();
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#getProperties()
    */
   @Override
   public Map<String, Object> getProperties()
   {
      return delegate.getProperties();
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#getReference(java.lang.Class, java.lang.Object)
    */
   @Override
   public <T> T getReference(final Class<T> arg0, final Object arg1)
   {
      return delegate.getReference(arg0, arg1);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#getTransaction()
    */
   @Override
   public EntityTransaction getTransaction()
   {
      return delegate.getTransaction();
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#isJoinedToTransaction()
    */
   @Override
   public boolean isJoinedToTransaction()
   {
      return delegate.isJoinedToTransaction();
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#isOpen()
    */
   @Override
   public boolean isOpen()
   {
      return delegate.isOpen();
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#joinTransaction()
    */
   @Override
   public void joinTransaction()
   {
      delegate.joinTransaction();
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#lock(java.lang.Object, javax.persistence.LockModeType)
    */
   @Override
   public void lock(final Object arg0, final LockModeType arg1)
   {
      delegate.lock(arg0, arg1);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#lock(java.lang.Object, javax.persistence.LockModeType, java.util.Map)
    */
   @Override
   public void lock(final Object arg0, final LockModeType arg1, final Map<String, Object> arg2)
   {
      delegate.lock(arg0, arg1, arg2);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#merge(java.lang.Object)
    */
   @Override
   public <T> T merge(final T arg0)
   {
      return delegate.merge(arg0);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#persist(java.lang.Object)
    */
   @Override
   public void persist(final Object arg0)
   {
      delegate.persist(arg0);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#refresh(java.lang.Object)
    */
   @Override
   public void refresh(final Object arg0)
   {
      delegate.refresh(arg0);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#refresh(java.lang.Object, javax.persistence.LockModeType)
    */
   @Override
   public void refresh(final Object arg0, final LockModeType arg1)
   {
      delegate.refresh(arg0, arg1);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#refresh(java.lang.Object, javax.persistence.LockModeType, java.util.Map)
    */
   @Override
   public void refresh(final Object arg0, final LockModeType arg1, final Map<String, Object> arg2)
   {
      delegate.refresh(arg0, arg1, arg2);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#refresh(java.lang.Object, java.util.Map)
    */
   @Override
   public void refresh(final Object arg0, final Map<String, Object> arg1)
   {
      delegate.refresh(arg0, arg1);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#remove(java.lang.Object)
    */
   @Override
   public void remove(final Object arg0)
   {
      delegate.remove(arg0);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#setFlushMode(javax.persistence.FlushModeType)
    */
   @Override
   public void setFlushMode(final FlushModeType arg0)
   {
      delegate.setFlushMode(arg0);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#setProperty(java.lang.String, java.lang.Object)
    */
   @Override
   public void setProperty(final String arg0, final Object arg1)
   {
      delegate.setProperty(arg0, arg1);
   }

   /*
    * (non-Javadoc)
    * @see javax.persistence.EntityManager#unwrap(java.lang.Class)
    */
   @Override
   public <T> T unwrap(final Class<T> arg0)
   {
      return delegate.unwrap(arg0);
   }
}

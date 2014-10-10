/**
 *
 */
package web30.persistence;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.StackObjectPool;
import org.apache.log4j.Logger;

/**
 *
 */
public class EntityManagerPool
{
   /** factory for pooled entity managers */
   public static final class PoolableEntityManagerFactory extends BasePoolableObjectFactory<PooledEntityManager>
   {
      /** entity manager factory */
      private final EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("web30",
            System.getProperties());

      /**
       * Constructor
       */
      public PoolableEntityManagerFactory()
      {
         super();
         final Logger log = Logger.getLogger(EntityManagerPool.class);

         final Map<String, Object> properties = emFactory.getProperties();
         log.debug("jdbc URL = " + properties.get("javax.persistence.jdbc.url"));
         if (log.isTraceEnabled())
         {
            final StringBuffer message = new StringBuffer(1024);
            message.append("Properties for PoolableEntityManagerFactory:");
            for (final String key : properties.keySet())
            {
               if (key.toLowerCase().contains("hibernate") || key.toLowerCase().contains("persistence"))
               {
                  message.append("\n\t").append(key).append("\t");
                  if (key.toLowerCase().contains("password"))
                  {
                     message.append("****");
                  }
                  else
                  {
                     message.append(properties.get(key));
                  }
               }
            }
            log.trace(message.toString());
         }
      }

      /**
       * Close the factory in order to release its resources and allow the service using it to be stopped safely.
       */
      public void close()
      {
         emFactory.close();
      }

      /*
       * (non-Javadoc)
       * @see org.apache.commons.pool.BasePoolableObjectFactory#destroyObject(java.lang.Object)
       */
      @Override
      public void destroyObject(final PooledEntityManager em) throws Exception
      {
         em.closeEntityManager();
      }

      @Override
      public PooledEntityManager makeObject() throws Exception
      {
         final EntityManager em = emFactory.createEntityManager();
         return new PooledEntityManager(em);
      }
   }

   /** the pool of database connections */
   private static StackObjectPool<PooledEntityManager> entityManagerPool;

   /**
    * get a database connection
    * 
    * @return a usable database connection
    */
   public static PooledEntityManager borrowEntityManager()
   {
      final Logger log = Logger.getLogger(EntityManagerPool.class);

      if (entityManagerPool == null)
      {
         synchronized (EntityManagerPool.class)
         {
            if (entityManagerPool == null)
            {
               entityManagerPool = createPool();
               log.debug("Entitity Manager Pool initialized.");
            }
         }
      }
      try
      {
         final PooledEntityManager em = entityManagerPool.borrowObject();
         log.trace("Borrowed database connection from Entitity Manager Pool.");
         return em;
      }
      catch (final Exception e)
      {
         log.error("Cannot borrow entity manager", e);
      }
      return null;
   }

   /**
    * Close the Entity Manager Pool to release its resources and allow the service using it to stop safely.
    * 
    * @throws Exception
    *            if the pool is not empty of active entity managers
    */
   public static void closeEntityManagerPool() throws Exception
   {
      final Logger log = Logger.getLogger(EntityManagerPool.class);

      log.debug("Attempting to close the Entity Manager Pool");
      ((PoolableEntityManagerFactory) entityManagerPool.getFactory()).close();
      entityManagerPool.close();
      log.trace("Entity Manager Pool closed.");
      entityManagerPool = null;
   }

   /**
    * @return a newly created pool
    */
   private static StackObjectPool<PooledEntityManager> createPool()
   {
      return new StackObjectPool<>(new PoolableEntityManagerFactory());
   }

   /**
    * return a borrowed database connection to the pool
    * 
    * @param em
    *           the connection being returned
    */
   public static void returnEntityManager(final PooledEntityManager em)
   {
      final Logger log = Logger.getLogger(EntityManagerPool.class);

      if (em == null)
      {
         throw new IllegalArgumentException();
      }

      try
      {
         // make sure no database objects stick around in the cache for the next user to get
         em.clear();
         entityManagerPool.returnObject(em);
         log.trace("Returned database connection to Entitity Manager Pool.");
      }
      catch (final Exception e)
      {
         log.error("Cannot return entity manager", e);
      }
   }

}

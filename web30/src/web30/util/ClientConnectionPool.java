/*
  */
package web30.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Configuration;

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.StackKeyedObjectPool;
import org.apache.log4j.Logger;

/**
 * Create a client pool that allows borrowing and returning clients based on pool key/name. Any objects to be registered
 * by each client must be added via addRegistrant(String) before the first client is borrowed/created.
 */
public class ClientConnectionPool
{
   /** the factory to create clients based on a pool key/name */
   static class PoolableClientFactory extends BaseKeyedPoolableObjectFactory<String, Client>
   {
      /**
       * @see org.apache.commons.pool.BasePoolableObjectFactory#destroyObject(Object)
       */
      @Override
      public void destroyObject(final String poolName, final Client client) throws Exception
      {
         client.close();
      }

      /**
       * @see org.apache.commons.pool.BasePoolableObjectFactory#makeObject()
       */
      @Override
      public Client makeObject(final String poolName) throws Exception
      {
         ClientBuilder builder = ClientBuilder.newBuilder();

         // set the configuration
         final Configuration config = ClientConnectionPool.configurations.get(poolName);
         if (config != null)
         {
            builder = builder.withConfig(config);
         }

         // set the SSL context, if there is one
         final SSLContext sslContext = ClientConnectionPool.sslContexts.get(poolName);
         if (sslContext != null)
         {
            builder = builder.sslContext(sslContext);
         }

         // register objects from the pool's registrants
         final List<?> list = ClientConnectionPool.registrants.get(poolName);
         if (list != null)
         {
            for (final Object registrant : list)
            {
               builder.register(registrant);
            }
         }

         // set properties from the pool's properties
         final Map<String, ?> map = ClientConnectionPool.properties.get(poolName);
         if (map != null)
         {
            for (final Map.Entry<String, ?> entry : map.entrySet())
            {
               builder.property(entry.getKey(), entry.getValue());
            }
         }

         // build the client instance from the above settings
         return builder.build();
      }
   }

   /** objects to be registered with each new client */
   static Map<String, List<?>> registrants = new HashMap<>();

   /** property map to be applied to each new client */
   static Map<String, Map<String, ?>> properties = new HashMap<>();

   /** configuration to be applied to each new client */
   static Map<String, Configuration> configurations = new HashMap<>();

   /** SSL context to be applied to each new client */
   static Map<String, SSLContext> sslContexts = new HashMap<>();

   /** the pool of clients */
   static StackKeyedObjectPool<String, Client> pool;

   /**
    * Add an object to be registered with each new client in a pool.
    * 
    * @param poolName
    *           the pool whose new clients should register the object
    * @param registrant
    *           the object to be registered
    */
   public static void addRegistrant(final String poolName, final Object registrant)
   {
      if (pool != null)
      {
         final Logger log = Logger.getLogger(ClientConnectionPool.class);

         // ES-1087 Log injection
         String clean = poolName.replace('\n', '_').replace('\r', '_');
         if (!poolName.equals(clean))
         {
            clean += " (Encoded)";
         }

         log.warn("adding registrant after pool is instantiated; old clients for pool " + clean
               + " will not have new registrants");
      }
      List<Object> list = (List<Object>) registrants.get(poolName);
      if (list == null)
      {
         list = new ArrayList<>();
         registrants.put(poolName, list);
      }
      list.add(registrant);
   }

   /**
    * borrow a {@link Client} from the client pool.
    * 
    * @param poolName
    *           the client pool to use
    * @return an available client
    * @throws Exception
    *            if no client is available
    * @see org.apache.commons.pool.impl.StackObjectPool#borrowObject()
    * @see #returnClient(PooledClient)
    */
   public static PooledClient borrowClient(final String poolName) throws Exception
   {
      if (pool == null)
      {
         synchronized (ClientConnectionPool.class)
         {
            if (pool == null)
            {
               pool = new StackKeyedObjectPool<>(new PoolableClientFactory());
            }
         }
      }
      final Client client = pool.borrowObject(poolName);
      return new PooledClient(client, poolName, registrants.get(poolName), properties.get(poolName));
   }

   /**
    * Convert a package name into a URL-like name. This is recommended to be used for each pool name but is not
    * required.
    * 
    * @param pkg
    *           Java package
    * @return a URL made from the package, e.g. "http://es.nga.mil/cdr/search" or "http://another"
    */
   public static String convertPackageToUrl(final Package pkg)
   {
      final StringBuilder b = new StringBuilder();
      if (pkg != null)
      {
         final String[] a = pkg.getName().split("\\.");
         if (a.length > 0)
         {
            b.append("http://");
         }

         if (a.length == 1)
         {
            b.append(a[0]);
         }
         else if (a.length == 2)
         {
            b.append(a[1]).append(".").append(a[0]);
         }
         else if (a.length > 2)
         {
            b.append(a[2]).append(".").append(a[1]).append(".").append(a[0]);
            for (int i = 3; i < a.length; i++)
            {
               b.append("/").append(a[i]);
            }
         }
      }
      return b.toString();
   }

   /**
    * Get the value of a property for a given pool, which is used when each new client is created.
    * 
    * @param poolName
    *           the pool
    * @param propertyName
    *           the property name
    * @return the property value
    */
   public static Object getProperty(final String poolName, final String propertyName)
   {
      final Map<String, Object> map = (Map<String, Object>) properties.get(poolName);
      if (map == null)
      {
         return null;
      }
      return map.get(propertyName);
   }

   /**
    * Return a client to the pool. Once this client is returned, do not use that client again. Instead, replace the
    * previous client with a new one from borrowClient(String). This may or may not be the same client instance used
    * previously.
    * 
    * @param pooledClient
    *           the pooled client, containing the pool name and the client to return
    * @throws Exception
    *            if the client cannot be returned
    * @see org.apache.commons.pool.impl.StackObjectPool#returnObject(java.lang.Object)
    * @see #borrowClient(String)
    */
   public static void returnClient(final PooledClient pooledClient) throws Exception
   {
      if (pooledClient != null)
      {
         pool.returnObject(pooledClient.getPoolName(), pooledClient.getClient());
      }
   }

   /**
    * Add a configuration to be used with each new client in a pool.
    * 
    * @param poolName
    *           the pool whose new clients should use this configuration
    * @param config
    *           the configuration for the new clients
    */
   // Es-1087: Unused code contains log message which may flag log manipulation
   public static void setConfiguration(final String poolName, final Configuration config)
   {
      if (pool != null)
      {
         final Logger log = Logger.getLogger(ClientConnectionPool.class);
         log.warn("setting configuration after pool is instantiated; old clients for pool " + poolName
               + " will not have this configuration");
      }
      configurations.put(poolName, config);
   }

   /**
    * Add a property to be set on each new client in a pool.
    * 
    * @param poolName
    *           the pool whose new clients should have this property
    * @param propertyName
    *           the property name
    * @param propertyValue
    *           the property value
    */
   public static void setProperty(final String poolName, final String propertyName, final Object propertyValue)
   {
      if (pool != null)
      {
         final Logger log = Logger.getLogger(ClientConnectionPool.class);
         log.warn("setting property after pool is instantiated; old clients for pool " + poolName
               + " will not have new properties");
      }
      Map<String, Object> map = (Map<String, Object>) properties.get(poolName);
      if (map == null)
      {
         synchronized (ClientConnectionPool.class)
         {
            map = (Map<String, Object>) properties.get(poolName);
            if (map == null)
            {
               map = new HashMap<>();
               properties.put(poolName, map);
            }
         }
      }
      map.put(propertyName, propertyValue);
   }

   /**
    * Add an SSL context to be used for each new client in a pool.
    * 
    * @param poolName
    *           the pool whose new clients should use this SSL context
    * @param sslContext
    *           the SSL context for the new clients
    */
   public static void setSslContext(final String poolName, final SSLContext sslContext)
   {
      if (pool != null)
      {
         final Logger log = Logger.getLogger(ClientConnectionPool.class);
         log.warn("adding SSL context after pool is instantiated; old clients for pool " + poolName
               + " will not have this SSL context");
      }
      sslContexts.put(poolName, sslContext);
   }
}

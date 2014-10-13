/**
 *
 */
package web30.util;

import java.io.Closeable;
import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;

/**
 * Contains the client, pool key/name, and list of registrants. Can be used to make a try-with-resource block for
 * auto-close.
 */
public class PooledClient implements Client, Closeable
{
   /** the logger */
   private final Logger log = Logger.getLogger(getClass());

   /** the client from the pool */
   private final Client client;

   /** the key/name of the client pool */
   private final String poolName;

   /** the registrants at the time of borrowing from the pool */
   private final List<?> registrants;

   /** properties at the time of borrowing from the pool */
   private final Map<String, ?> properties;

   /**
    * @param client
    *           the client
    * @param poolName
    *           the pool key/name
    * @param registrants
    *           the objects registered to the client by the pool
    * @param properties
    *           the client properties set by the pool
    */
   PooledClient(final Client client, final String poolName, final List<?> registrants, final Map<String, ?> properties)
   {
      this.client = client;
      this.poolName = poolName;
      this.registrants = registrants;
      this.properties = properties;
   }

   /*
    * (non-Javadoc)
    * @see java.io.Closeable#close()
    */
   @Override
   public void close()
   {
      try
      {
         ClientConnectionPool.returnClient(this);
      }
      catch (final Exception e)
      {
         log.error("Cannot return client to the pool", e);
         // throw new IOException("error while returning client to pool " + poolName, e);
      }
   }

   /**
    * @see javax.ws.rs.client.Client#close()
    */
   public void closeClient()
   {
      client.close();
   }

   /**
    * @return the client
    */
   public Client getClient()
   {
      return client;
   }

   /**
    * @return the configuration
    * @see javax.ws.rs.core.Configurable#getConfiguration()
    */
   @Override
   public Configuration getConfiguration()
   {
      return client.getConfiguration();
   }

   /**
    * @return the verifier
    * @see javax.ws.rs.client.Client#getHostnameVerifier()
    */
   @Override
   public HostnameVerifier getHostnameVerifier()
   {
      return client.getHostnameVerifier();
   }

   /**
    * @return the poolName
    */
   public String getPoolName()
   {
      return poolName;
   }

   /**
    * @return the client properties set by the pool
    */
   public Map<String, ?> getProperties()
   {
      return properties;
   }

   /**
    * @return the objects registered to the client by the pool
    */
   public List<?> getRegistrants()
   {
      return registrants;
   }

   /**
    * @return the SSL context
    * @see javax.ws.rs.client.Client#getSslContext()
    */
   @Override
   public SSLContext getSslContext()
   {
      return client.getSslContext();
   }

   /**
    * @param link
    *           the link to invoke
    * @return the invocation builder
    * @see javax.ws.rs.client.Client#invocation(javax.ws.rs.core.Link)
    */
   @Override
   public Builder invocation(final Link link)
   {
      return client.invocation(link);
   }

   /**
    * @param name
    *           the property name to set
    * @param value
    *           the new value of the property
    * @return this
    * @see javax.ws.rs.core.Configurable#property(java.lang.String, java.lang.Object)
    */
   @Override
   public Client property(final String name, final Object value)
   {
      return client.property(name, value);
   }

   /**
    * @param providerClass
    *           the class to register
    * @return this
    * @see javax.ws.rs.core.Configurable#register(java.lang.Class)
    */
   @Override
   public Client register(final Class<?> providerClass)
   {
      return client.register(providerClass);
   }

   /**
    * @param providerClass
    *           the first class to register
    * @param contracts
    *           the rest of the classes to register
    * @return this
    * @see javax.ws.rs.core.Configurable#register(java.lang.Class, java.lang.Class[])
    */
   @Override
   public Client register(final Class<?> providerClass, final Class<?>... contracts)
   {
      return client.register(providerClass, contracts);
   }

   /**
    * @param providerClass
    * @param bindingPriority
    * @return this
    * @see javax.ws.rs.core.Configurable#register(java.lang.Class, int)
    */
   @Override
   public Client register(final Class<?> providerClass, final int bindingPriority)
   {
      return client.register(providerClass, bindingPriority);
   }

   /**
    * @param providerClass
    * @param contracts
    * @return this
    * @see javax.ws.rs.core.Configurable#register(java.lang.Class, java.util.Map)
    */
   @Override
   public Client register(final Class<?> providerClass, final Map<Class<?>, Integer> contracts)
   {
      return client.register(providerClass, contracts);
   }

   /**
    * @param provider
    * @return this
    * @see javax.ws.rs.core.Configurable#register(java.lang.Object)
    */
   @Override
   public Client register(final Object provider)
   {
      return client.register(provider);
   }

   /**
    * @param provider
    * @param contracts
    * @return this
    * @see javax.ws.rs.core.Configurable#register(java.lang.Object, java.lang.Class[])
    */
   @Override
   public Client register(final Object provider, final Class<?>... contracts)
   {
      return client.register(provider, contracts);
   }

   /**
    * @param provider
    * @param bindingPriority
    * @return this
    * @see javax.ws.rs.core.Configurable#register(java.lang.Object, int)
    */
   @Override
   public Client register(final Object provider, final int bindingPriority)
   {
      return client.register(provider, bindingPriority);
   }

   /**
    * @param provider
    * @param contracts
    * @return this
    * @see javax.ws.rs.core.Configurable#register(java.lang.Object, java.util.Map)
    */
   @Override
   public Client register(final Object provider, final Map<Class<?>, Integer> contracts)
   {
      return client.register(provider, contracts);
   }

   /**
    * @param link
    * @return the target
    * @see javax.ws.rs.client.Client#target(javax.ws.rs.core.Link)
    */
   @Override
   public WebTarget target(final Link link)
   {
      return client.target(link);
   }

   /**
    * @param uri
    * @return the target
    * @see javax.ws.rs.client.Client#target(java.lang.String)
    */
   @Override
   public WebTarget target(final String uri)
   {
      return client.target(uri);
   }

   /**
    * @param uri
    * @return the target
    * @see javax.ws.rs.client.Client#target(java.net.URI)
    */
   @Override
   public WebTarget target(final URI uri)
   {
      return client.target(uri);
   }

   /**
    * @param uriBuilder
    * @return the target
    * @see javax.ws.rs.client.Client#target(javax.ws.rs.core.UriBuilder)
    */
   @Override
   public WebTarget target(final UriBuilder uriBuilder)
   {
      return client.target(uriBuilder);
   }

}

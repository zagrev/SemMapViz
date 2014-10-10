/**
 *
 */
package web30.util;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.wadl.config.WadlGeneratorConfig;

/**
 * Extend this class to conveniently create a runnable Jersey service. Only the String... constructor and
 * getClassesToRegister() need to be defined in the extending class, though it may be useful to also override other
 * functions. See each function's Javadoc comments for more information.
 */
public abstract class JerseyService
{
   /** runnable thread that waits on itself to be notified */
   public class MonitorRunner implements Runnable
   {
      @Override
      public void run()
      {
         final Logger log = Logger.getLogger(JerseyService.class);
         log.debug("service monitor will wait until stop() has been called");
         synchronized (this)
         {
            try
            {
               this.wait();
               log.debug("service monitor complete");
            }
            catch (final InterruptedException e)
            {
               log.debug("service monitor interrupted");
            }
         }
      }
   }

   /** URI scheme for HTTP */
   public static String URI_SCHEME_HTTP = "http";

   /** URI scheme for HTTPS */
   public static String URI_SCHEME_HTTPS = "https";

   /** hostname */
   private String host = "0.0.0.0";

   /** synchronization monitor */
   private final MonitorRunner monitorRunner = new MonitorRunner();
   /** the command line options for this service */
   private Options options;
   /** port */
   private int port = 12345;
   /** the properties filename for this service */
   private String properties = "config.properties";
   /** the properties option for this service */
   private Option propertiesOption;
   /** the HTTP server */
   private HttpServer server;
   /** cache of the SSL config, for start() and getUri() */
   private SSLEngineConfigurator sslEngingConfigurator;

   /**
    * This is called by start().
    * 
    * @return a set of classes that should be registered in the ResourceConfig registerClasses(Class<?>...)
    */
   protected abstract Class<?>[] getClassesToRegister();

   /**
    * @return the hostname
    */
   public String getHost()
   {
      return host;
   }

   /**
    * get the options for this service. This implementation includes the host, port, and properties options. This will
    * only create the options once.
    * 
    * @return the options for this service.
    */
   public Options getOptions()
   {
      if (options == null)
      {
         options = new Options();

         Option option = new Option("host", "The IP address on which to listen. Defaults to " + getHost());
         option.setArgName("ip");
         options.addOption(option);

         option = new Option("port", "The port number on which to listen. Defaults to " + getPort());
         option.setArgName("port");
         options.addOption(option);

         options.addOption(getPropertiesOption());
      }
      return options;
   }

   /**
    * @return the port
    */
   public int getPort()
   {
      return port;
   }

   /**
    * @return the properties
    */
   public String getProperties()
   {
      return properties;
   }

   /**
    * gets the default properties option for the service. This will create the option if it doesn't exist.
    * 
    * @return the properties option
    */
   public Option getPropertiesOption()
   {
      if (propertiesOption == null)
      {
         propertiesOption = new Option("properties", "The configuration properties file for this service. Defaults to "
               + getProperties());
         propertiesOption.setArgName("file");
      }
      return propertiesOption;
   }

   /**
    * @return a singleton instance of the SSL Engine Configurator
    */
   private SSLEngineConfigurator getSingletonSslEngineConfigurator()
   {
      if (sslEngingConfigurator == null)
      {
         sslEngingConfigurator = getSslEngineConfigurator();
      }
      return sslEngingConfigurator;
   }

   /**
    * Get/create the SSL Engine Configurator with SSL Context. Unless overridden, this simply returns null to indicate
    * no SSL configuration. This is called by start (indirectly, via a private getSingletonSslEngineConfigurator()).
    * 
    * @return null
    */
   protected SSLEngineConfigurator getSslEngineConfigurator()
   {
      return null;
   }

   /**
    * This calls the private getSingletonSslEngineConfigurator() to determine whether to use "http" or "https".
    * 
    * @return the URI, built from the current hostname and port
    */
   public URI getUri()
   {
      String scheme = URI_SCHEME_HTTP;
      if (getSingletonSslEngineConfigurator() != null)
      {
         scheme = URI_SCHEME_HTTPS;
      }

      // use fromPath("/") to get the trailing slash after the port
      return UriBuilder.fromPath("/").scheme(scheme).host(getHost()).port(getPort()).build();
   }

   /**
    * Override this to modify ResourceConfig further, if needed. This is called by start().
    * 
    * @param config
    *           the ResourceConfig to modify
    */
   protected void modifyConfig(final ResourceConfig config)
   {
      // do nothing, but allow children to override if needed
   }

   /**
    * @param hostname
    *           the hostname to set
    */
   public void setHost(final String hostname)
   {
      this.host = hostname;
   }

   /**
    * @param port
    *           the port to set
    */
   public void setPort(final int port)
   {
      this.port = port;
   }

   /**
    * @param properties
    *           the properties to set
    */
   public void setProperties(final String properties)
   {
      this.properties = properties;
   }

   /**
    * @param propertiesOption
    *           the propertiesOption to set
    */
   public void setPropertiesOption(final Option propertiesOption)
   {
      this.propertiesOption = propertiesOption;
   }

   /**
    * Start the Grizzly HTTP server and launch a thread that waits until stop() is called. This calls the following:
    * <ol>
    * <li>getClassesToRegister()</li>
    * <li>modifyConfig(ResourceConfig)</li>
    * <li>getSslEngineConfigurator() (rather, the private getSingletonSslEngineConfigurator())</li>
    * </ol>
    */
   public synchronized void start()
   {
      final Logger log = Logger.getLogger(JerseyService.class);
      final Class<?>[] classesToRegister = getClassesToRegister();

      if (classesToRegister == null || classesToRegister.length == 0)
      {
         throw new IllegalArgumentException("must register at least one class for the service to work");
      }

      final ResourceConfig config = new ResourceConfig();
      config.registerClasses(classesToRegister);
      config.property(ServerProperties.WADL_GENERATOR_CONFIG, WadlGeneratorConfig.class);

      // allow custom instances of filters, etc.
      modifyConfig(config);

      // allow SSL config
      final SSLEngineConfigurator sslConfig = getSingletonSslEngineConfigurator();

      final URI uri = getUri();
      log.info("starting server on " + uri);

      server = GrizzlyHttpServerFactory.createHttpServer(uri, config, sslConfig != null, sslConfig);
      // server is already started by Grizzly
      startMonitorThread();
   }

   /**
    * start the monitor thread
    */
   private void startMonitorThread()
   {
      synchronized (monitorRunner)
      {
         final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory()
         {
            @Override
            public Thread newThread(final Runnable r)
            {
               final Thread t = new Thread(r);
               t.setName("StatusMonitor");
               return t;
            }
         });
         executor.execute(monitorRunner);
         executor.shutdown();
      }
   }

   /**
    * stop the service and notify the monitor thread to stop waiting and terminate
    */
   public synchronized void stop()
   {
      if (server != null)
      {
         final Logger log = Logger.getLogger(JerseyService.class);
         final URI uri = getUri();

         log.debug("stopping monitor on " + uri);
         stopMonitorThread();

         log.debug("stopping server on " + uri);
         server.stop();
         // must nullify server; it can't be started again, anyway
         server = null;
      }
   }

   /**
    * notify the monitor thread to stop waiting and terminate
    */
   private void stopMonitorThread()
   {
      synchronized (monitorRunner)
      {
         monitorRunner.notify();
      }
   }

}

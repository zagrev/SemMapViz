package web30.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.PropertyConfigurator;

/**
 * This tool will configure any object that conforms to the ICofnigiuration interface. It does this by iterating through
 * the command line options and setting the properties on the object that match the option name.
 */
public class ServiceConfigurator
{
   /** the object to be configured */
   private final IConfiguration service;

   /** the name of the object to display when the command line options are incorrect */
   private final String serviceName;

   /**
    * create a configurator with the given name, to configure the given object.
    * 
    * @param commandLineName
    *           the name to display with the command line help
    * @param service
    *           the object to configure
    */
   public ServiceConfigurator(final String commandLineName, final IConfiguration service)
   {
      this.serviceName = commandLineName;
      this.service = service;
   }

   /**
    * Set the default properties from the inputStream
    * 
    * @param input
    *           Input stream containing the properties
    * @exception IOException
    *               if an input/output error occurs
    * @throws NoSuchMethodException
    *            on error
    * @throws InvocationTargetException
    *            on error
    * @throws IllegalAccessException
    *            on error
    */
   public void loadFromProperties(final InputStream input) throws IOException, IllegalAccessException,
         InvocationTargetException, NoSuchMethodException
   {
      if (input == null)
      {
         throw new IllegalArgumentException("Properties InputStream is null");
      }

      final Properties properties = System.getProperties();
      properties.load(input);
      loadFromProperties(properties);
   }

   /**
    * @param properties
    *           the properties to load
    * @throws NoSuchMethodException
    *            on error
    * @throws InvocationTargetException
    *            on error
    * @throws IllegalAccessException
    *            on error
    */
   public void loadFromProperties(final Properties properties) throws IllegalAccessException,
         InvocationTargetException, NoSuchMethodException
   {
      if (service == null || properties == null)
      {
         return;
      }

      final Options options = service.getOptions();
      final Option optProperties = service.getPropertiesOption();
      final String prefix = service.getClass().getPackage().getName() + ".";

      final ArrayList<Option> optList = new ArrayList<>(options.getOptions());

      for (final Option option : optList)
      {
         if (option != optProperties)
         {
            final String optName = option.getOpt();
            final String optionName = prefix + optName;

            String value = properties.getProperty(optionName);
            if (value == null)
            {
               value = properties.getProperty(optName);
            }
            if (value != null)
            {
               BeanUtils.setProperty(service, optName, value);
            }
         }
      }
   }

   /**
    * @param propertyPath
    *           the path to the properties file
    * @throws IOException
    *            if the file cannot be read
    * @throws NoSuchMethodException
    *            if the property is not found on the object
    * @throws InvocationTargetException
    *            if the permissions do not allow changing the object
    * @throws IllegalAccessException
    *            if the object has other problems
    */
   public void loadFromProperties(final String propertyPath) throws IOException, IllegalAccessException,
         InvocationTargetException, NoSuchMethodException
   {
      final Properties properties = System.getProperties();

      try (final FileReader reader = new FileReader(propertyPath))
      {
         properties.load(reader);
         loadFromProperties(properties);
      }
   }

   /**
    * This will parse the command line. It parses all the arguments and sets the properties on the object to be
    * configured that have the names that match the option name
    * 
    * @param args
    *           the command line arguments to parse
    * @throws ParseException
    *            if the command line is incorrect
    * @throws FileNotFoundException
    *            if the properties files specified on the command line is incorrect
    * @throws IOException
    *            if the properties file cannot be read
    * @throws IllegalAccessException
    *            if the object doesn't allow configuration
    * @throws InvocationTargetException
    *            if the object doesn't allow configuration
    * @throws NoSuchMethodException
    *            if the option name doesnt' match a property name on the object
    */
   public void parseCommandLine(final String[] args) throws ParseException, FileNotFoundException, IOException,
         IllegalAccessException, InvocationTargetException, NoSuchMethodException
   {
      if (service == null)
      {
         return;
      }

      final Options options = service.getOptions();
      final Option optProperties = service.getPropertiesOption();
      try
      {
         final CommandLineParser parser = new BasicParser();
         final CommandLine values = parser.parse(options, args);

         // load in the properties file, if one was specified
         if (optProperties != null && values.hasOption(optProperties.getOpt()))
         {
            try (FileInputStream is = new FileInputStream(values.getOptionValue(optProperties.getOpt())))
            {
               final Properties properties = System.getProperties();
               properties.load(is);
               PropertyConfigurator.configure(properties);
               loadFromProperties(properties);
            }
         }
         // if no properties file, then attempt to configure Log4j, in case any system properties were set as JVM args
         else
         {
            PropertyConfigurator.configure(System.getProperties());
         }

         // set each property of the service, using the option name with reflection
         final ArrayList<Option> optList = new ArrayList<>(options.getOptions());
         for (final Option opt : optList)
         {
            if (opt != optProperties)
            {
               final String fieldName = opt.getOpt();
               Object value = null;

               if (opt.hasArg())
               {
                  value = values.getOptionValue(fieldName);
               }
               else if (values.hasOption(fieldName))
               {
                  value = "true";
               }

               if (value != null)
               {
                  BeanUtils.setProperty(service, fieldName, value);
               }
            }
         }
      }
      catch (final ParseException e)
      {
         final HelpFormatter formatter = new HelpFormatter();
         formatter.printHelp(serviceName, options);
         throw e;
      }
   }

}

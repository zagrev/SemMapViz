package web30.util;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * the interface that states that a service supports the ServiceConfigurator
 */
public interface IConfiguration
{
   /**
    * return the options collection of all the possible options
    * 
    * @return the Options to process
    */
   Options getOptions();

   /**
    * get the option that represents a properties file so that this option can be loaded before all the other command
    * line options.
    * 
    * @return the option for the properties file
    */
   Option getPropertiesOption();

}

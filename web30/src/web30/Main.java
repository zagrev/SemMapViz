/**
 * 
 */
package web30;

import web30.util.ServiceConfigurator;

/**
 * 
 */
public class Main
{

   /**
    * @param args
    *           the command line arguments for this service
    * @throws Exception
    *            on error
    */
   public static void main(final String[] args) throws Exception
   {
      final Web30Service service = new Web30Service();
      final ServiceConfigurator configurator = new ServiceConfigurator("web30", service);
      configurator.parseCommandLine(args);

      service.start();
   }

}

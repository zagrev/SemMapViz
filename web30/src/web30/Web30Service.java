/**
 * 
 */
package web30;

import web30.util.FileServer;
import web30.util.IConfiguration;
import web30.util.JerseyService;
import web30.util.VersionHandler;

/**
 * 
 */
public class Web30Service extends JerseyService implements IConfiguration
{

   /*
    * (non-Javadoc)
    * @see web30.util.JerseyService#getClassesToRegister()
    */
   @Override
   protected Class<?>[] getClassesToRegister()
   {
      final Class<?>[] classes =
      { TweetHandler.class, FileServer.class, VersionHandler.class };
      return classes;
   }
}

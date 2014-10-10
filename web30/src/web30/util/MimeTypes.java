package web30.util;

/**
 *
 */

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.digester3.Digester;
import org.apache.log4j.Logger;

/**
 *
 */
public class MimeTypes
{
   /** the known mime types. these are normally loaded from mime-types.xml */
   private static HashMap<String, String> mimeTypes;

   /**
    * create the mime type table
    */
   private static void initialize()
   {
      final Digester digester = new Digester();
      digester.addObjectCreate("*/mime-mapping", Mapping.class);
      digester.addBeanPropertySetter("*/extension", "extension");
      digester.addBeanPropertySetter("*/mime-type", "mimeType");
      digester.addSetNext("*/mime-mapping", "add");

      final ArrayList<Mapping> mappingList = new ArrayList<>();
      digester.push(mappingList);

      final URL resource = MimeTypes.class.getResource("mime-types.xml");
      try
      {
         digester.parse(resource);
      }
      catch (final Exception e)
      {
         final Logger log = Logger.getLogger(MimeTypes.class);
         if (resource == null)
         {
            log.error("Cannot find mime-types.xml in " + MimeTypes.class.getPackage().toString());
         }
         log.error("Cannot load mime types", e);
      }

      mimeTypes = new HashMap<>();
      for (final Mapping m : mappingList)
      {
         mimeTypes.put(m.getExtension(), m.getMimeType());
      }
   }

   /**
    * lookup the mime type of the a resource using the resources file name extension
    * 
    * @param extension
    *           the file extension
    * @return the mime type for the given extension
    */
   static public String lookup(final String extension)
   {
      if (mimeTypes == null)
      {
         synchronized (MimeTypes.class)
         {
            if (mimeTypes == null)
            {
               initialize();
            }
         }
      }
      String type = mimeTypes.get(extension);
      if (type == null)
      {
         type = "application/octet-stream";
      }
      return type;
   }
}

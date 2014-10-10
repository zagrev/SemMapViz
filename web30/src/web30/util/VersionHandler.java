package web30.util;

/**
 *
 */

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 *
 */
@Path("/version")
public class VersionHandler
{
   /** the logger */
   private final Logger log = Logger.getLogger(getClass());

   /**
    * @param attrKeySet
    *           attributes to dump
    */
   private void dumpAttribute(final Attributes attrKeySet)
   {
      for (final Object attrKey : attrKeySet.keySet())
      {
         log.trace("  " + attrKey + " = " + attrKeySet.get(attrKey));
      }
   }

   /**
    * returns the version number of the current application
    * 
    * @param uriInfo
    *           the uri information context
    * @return the version information as plain text
    */
   @GET
   @Produces(MediaType.TEXT_PLAIN)
   public Response getVersion(@Context final UriInfo uriInfo)
   {
      log.debug("getVersion from " + uriInfo.getRequestUri().toString());

      try
      {
         final URL manifestUrl = getClass().getResource("/META-INF/MANIFEST.MF");
         log.debug(manifestUrl);
         @SuppressWarnings("resource")
         InputStream input = manifestUrl.openStream();
         if (log.isTraceEnabled())
         {
            final String contents = IOUtils.toString(input);
            log.trace("contents = " + contents);
            input = new ByteArrayInputStream(contents.getBytes());
         }
         if (input != null)
         {
            try
            {
               final Manifest manifest = new Manifest(input);
               final Attributes attributes = manifest.getMainAttributes();

               if (log.isTraceEnabled())
               {
                  log.trace("MAIN Attributes");
                  dumpAttribute(attributes);

                  final Map<String, Attributes> entries = manifest.getEntries();
                  for (final String key : entries.keySet())
                  {
                     log.trace("KEY = " + key);
                     final Attributes attrKeySet = entries.get(key);
                     dumpAttribute(attrKeySet);
                  }
               }

               String buildDate = attributes.getValue("Built-On");
               if (buildDate == null)
               {
                  buildDate = "";
               }

               String application = attributes.getValue("Application");
               if (application == null)
               {
                  application = "";
               }

               String version = attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
               if (version == null)
               {
                  version = "";
               }

               input.close();
               input = getClass().getResourceAsStream("version.txt");
               if (input != null)
               {
                  String output = IOUtils.toString(input);
                  output = output.replace("%%VERSION%%", version).replace("%%BUILD%%", buildDate)
                        .replace("%%APPLICATION%%", application);
                  return Response.ok(output).build();
               }
               return Response.serverError().entity("Cannot get version template").build();
            }
            catch (final IOException e)
            {
               log.error("Cannot read the manifest", e);
               return Response.serverError().entity("Cannot read the manifest").build();
            }
            finally
            {
               if (input != null)
               {
                  input.close();
               }
            }
         }
      }
      catch (final Throwable e)
      {
         // ignore this
      }
      log.error("Cannot find any manifests");
      return Response.serverError().entity("Cannot find any manifests").build();
   }
}

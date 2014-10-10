package web30.util;

/**
 *
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

/**
 * Serve files from the file system or classpath.
 */
@Path("/")
public class FileServer
{
   /** the logger */
   private final Logger log = Logger.getLogger(getClass());
   /** the default classpath location for the visible web content */
   private static String classpathLocation = "/WebContent/";
   /** the directory that contains the visible web content */
   private static String localFileDirectory = "resources/WebContent/";
   /** the 404 error page template */
   private static String template404;
   /** the value for the Cache-Control header's max-age property */
   private static final int CACHE_MAX_AGE_IN_SECONDS = 5;

   /** the requested path, sanitized to disallow log injection */
   private String sanitizedPath;

   /**
    * get the 404 page and return it as a Response
    * 
    * @param path
    *           the request path that was not found
    * @return the newly created 404 response using the 404 resource page.
    */
   public Response create404NotFoundResponse(final String path)
   {
      String page = null;

      if (template404 == null)
      {
         try (InputStream input = loadResource("404.html"))
         {
            if (input == null)
            {
               throw new Exception("Resource 404.html not found!");
            }
            template404 = IOUtils.toString(input);
         }
         catch (final Exception e)
         {
            log.error("unable to get 404.html: " + e.getMessage());
            template404 = "<html><body><h1>404 Not Found: %%REQUESTPATH%%</h1></body></html>";
         }
      }
      page = template404.replaceAll("%%REQUESTPATH%%", StringEscapeUtils.escapeHtml(path));

      return Response.status(404).type(MediaType.TEXT_HTML_TYPE).entity(page).build();
   }

   /**
    * Get the requested file from the path. The media type is determined based on the file extension, if there is one.
    * The response should include both the Last-Modifed and Cache-Control headers. If the request includes the
    * If-Modified-Since header with a value >= Last-Modified, then a 304 is returned.
    * 
    * @param path
    *           the path from which to get the resource, either from the file system or the classpath
    * @param ifModifiedSince
    *           the HTTP header indicating that the client will accept a 304 Not Modified if the file has not been
    *           modified since this time
    * @param uriInfo
    *           the request URI info
    * @return a 200 Ok response containing the file stream, or a 304 Not Modified, or a 404 Not Found
    */
   @SuppressWarnings("resource")
   @GET
   @Path("{path:.*}")
   public Response getFile(@PathParam("path") final String path,
         @HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) final Date ifModifiedSince, @Context final UriInfo uriInfo)
   {
      log.debug("getFile from " + uriInfo.getRequestUri().toString());

      final Response response;
      sanitizedPath = path;

      // get the last modified date, if available
      final Date lastModified = getLastModified(path);

      // if If-Modified-Since is not older than lastModified, then create a 304 Not Modified
      if (lastModified != null && ifModifiedSince != null && ifModifiedSince.compareTo(lastModified) >= 0)
      {
         response = Response.notModified().build();
      }
      // otherwise, attempt to get the file/resource contents
      else
      {
         final InputStream resource = loadResource(path);

         // if we have a valid resource, then create a 200 OK with the resource contents
         if (resource != null)
         {
            final String fileType = getMediaType(path);
            final ResponseBuilder responseBuilder = Response.ok(resource, fileType);

            // add cache controls if we have a last-modified time
            if (lastModified != null)
            {
               responseBuilder
               // this allows a client to use If-Modified-Since on future requests
                     .header(HttpHeaders.LAST_MODIFIED, lastModified)
                     // this requires the browser to use our cache settings
                     .header(HttpHeaders.CACHE_CONTROL, "max-age=" + CACHE_MAX_AGE_IN_SECONDS + ", must-revalidate");
            }

            response = responseBuilder.build();
         }
         // if we don't have a valid resource, then create a 404 Not Found
         else
         {
            response = create404NotFoundResponse(path);
         }
      }

      // set the log level based on the type of status
      final int status = response.getStatus();
      final String msg = status + " " + sanitizedPath;
      if (status < 400)
      {
         log.trace(msg);
      }
      else if (status < 500)
      {
         log.debug(msg);
      }
      else
      {
         log.error(msg);
      }

      // returned stream will be closed by Jersey
      return response;
   }

   /**
    * get the default page
    * 
    * @param uriInfo
    *           the request URI info, including the query params
    * @return the index.html page
    */
   @GET
   public Response getHome(@Context final UriInfo uriInfo)
   {
      log.debug("getHome from " + uriInfo.getRequestUri().toString());
      final UriBuilder newUri = uriInfo.getRequestUriBuilder().path("index.html");
      return Response.temporaryRedirect(newUri.build()).build();
   }

   /**
    * Get the last modified date of the file indicated by the path, from the file system or the classpath. The date is
    * truncated to a resolution of 1 second. Note that the private static variable jarLastModified will be set if the
    * classpath resource was a JAR resource, so that all future calls to getLastModified will simply return
    * jarLastModified without making further attempts to read files and such.
    * 
    * @param path
    *           the path of the file
    * @return the last modified date, or null if it cannot be determined
    */
   private Date getLastModified(final String path)
   {
      try
      {
         long lastModified = 0;
         log.trace("FileServer.getLastModified: " + sanitizedPath);

         // first, check it as a file path
         File file = new File(localFileDirectory, path);
         if (file.canRead())
         {
            lastModified = file.lastModified();
            log.trace("getting Last-Modified from file");
         }

         // next, try to get it as a classpath resource
         if (lastModified <= 0)
         {
            final String massagedPath = classpathLocation + path.replace("\\", "/");
            final URL url = FileServer.class.getResource(massagedPath);
            final String protocol = url.getProtocol().toLowerCase();
            if (protocol.equals("file"))
            {
               file = new File(url.getFile());
               if (file.canRead())
               {
                  lastModified = file.lastModified();
                  log.trace("getting Last-Modified from file URL");
               }
            }
            // if this is a JAR file resource, then the best we can do is to obtain
            // the JAR file's last-modified time from the file system
            else if (protocol.equals("jar"))
            {
               final JarURLConnection jarUrl = (JarURLConnection) url.openConnection();
               file = new File(jarUrl.getJarFile().getName());
               if (file.canRead())
               {
                  lastModified = file.lastModified();
                  log.trace("getting Last-Modified from JAR file URL");
               }
            }
         }

         if (lastModified > 0)
         {
            // truncate time to a resolution of 1 second, as the HTTP headers do not include milliseconds
            final Date date = new Date(lastModified / 1000 * 1000);
            return date;
         }
      }
      catch (final Exception e)
      {
         // Errors at this point are most likely due to this path being Not Found,
         // so ignore the error.
         // These are not the files we're looking for. Move along.
      }

      return null;
   }

   /**
    * get the mime type for the given filename
    * 
    * @param path
    *           the path of the file to get the mime type for
    * @return the mime type, or application/octet-stream if unknown.
    */
   public String getMediaType(final String path)
   {
      final int index = path.lastIndexOf(".");
      String extension = path;
      if (index >= 0)
      {
         extension = path.substring(index + 1, path.length());
      }
      final String type = MimeTypes.lookup(extension);
      log.trace(type + " " + sanitizedPath);
      return type;
   }

   /**
    * Load the resource from the resource location. The resource location is either the local file directory or the
    * classpath resources. The InputStream must be closed, which Jersey will do if it is used as the Response entity.
    * 
    * @param path
    *           the resource to load
    * @return the resource input stream, or {@code null} if not found
    */
   @SuppressWarnings("resource")
   private InputStream loadResource(final String path)
   {
      InputStream resource = null;
      log.trace("FileServer.getFile: " + sanitizedPath);

      try
      {
         resource = new FileInputStream(new File(localFileDirectory, path));
      }
      catch (final IOException | IllegalArgumentException e)
      {
         log.trace("File or directory not found.");
         // ignore missing file; we'll attempt to load it from the class path, next
      }

      // only use the classpath version if no file found
      if (resource == null)
      {
         final String massagedPath = classpathLocation + path.replace("\\", "/");
         log.trace("Attempting getResourceAsStream(" + sanitizedPath + ")...");
         resource = FileServer.class.getResourceAsStream(massagedPath);
      }
      return resource;
   }
}

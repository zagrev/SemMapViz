/**
 * 
 */
package web30;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * 
 */
@Path("tweet")
public class TweetHandler
{
   /**
    * @param upperLeft
    * @param upperRight
    * @param lowerLeft
    * @param lowerRight
    * @return the tweets in the given bounding box
    */
   @GET
   public Response getTweets(@QueryParam("ul") final Double upperLeft, @QueryParam("ur") final Double upperRight,
         @QueryParam("ll") final Double lowerLeft, @QueryParam("lr") final Double lowerRight)
   {
      return Response.serverError().build();
   }
}

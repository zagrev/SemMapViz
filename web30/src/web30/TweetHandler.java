/**
 * 
 */
package web30;

import java.util.Date;

import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import web30.persistence.EntityManagerPool;
import web30.persistence.PooledEntityManager;

/**
 * 
 */
@Path("tweet")
public class TweetHandler
{
   /** the logger */
   private static Logger log = Logger.getLogger(TweetHandler.class);

   /**
    * get the range of times for which tweets are available.
    * 
    * @return a {@link TimeRange} that contains the min and max date and time for the data
    */
   @Path("maprange")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response getMapRange()
   {
      log.debug("getMapRange");
      try (PooledEntityManager em = EntityManagerPool.borrowEntityManager())
      {
         // remember, the ctor takes North, East, West, South
         final TypedQuery<BoundingBox> q = em.createQuery(
               "select new web30.BoundingBox( max(t.lon), max(t.lat), min(t.lat), min(t.lon)) from TwitterStream t",
               BoundingBox.class);
         final BoundingBox range = q.getSingleResult();

         return Response.ok(range).build();
      }
      catch (final Throwable t)
      {
         log.error("Cannot get the map range", t);
         return Response.serverError().entity("Map size is currently unavailable").build();
      }
   }

   /**
    * get the range of times for which tweets are available.
    * 
    * @return a {@link TimeRange} that contains the min and max date and time for the data
    */
   @Path("timerange")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response getTimeRange()
   {
      log.debug("getTimeRange");
      try (PooledEntityManager em = EntityManagerPool.borrowEntityManager())
      {
         final TypedQuery<TimeRange> q = em.createQuery(
               "select new web30.TimeRange( min(t.time), max(t.time)) from TwitterStream t", TimeRange.class);
         final TimeRange range = q.getSingleResult();

         return Response.ok(range).build();
      }
      catch (final Throwable t)
      {
         log.error("Cannot get the time range", t);
         return Response.serverError().entity("Times are currently unavailable").build();
      }
   }

   /**
    * @param northParam
    * @param southParam
    * @param eastParam
    * @param westParam
    * @param minDateParam
    * @param maxDateParam
    * @return the tweets for the given location and time
    */
   @GET
   public Response getTweets(@QueryParam("north") final Double northParam,
         @QueryParam("south") final Double southParam, @QueryParam("east") final Double eastParam,
         @QueryParam("west") final Double westParam, @QueryParam("mindate") final Date minDateParam,
         @QueryParam("maxdate") final Date maxDateParam)
   {
      log.debug("getTweets");
      Double north = northParam;
      Double south = southParam;
      Double east = eastParam;
      Double west = westParam;

      // make sure all the parameters are set to something useful
      if (north == null)
      {
         north = Double.valueOf(90.0);
      }
      if (south == null)
      {
         south = Double.valueOf(-90.0);
      }
      if (east == null)
      {
         east = Double.valueOf(180.0);
      }
      if (west == null)
      {
         west = Double.valueOf(-180.0);
      }

      // make sure the min is less than the max
      if (north.doubleValue() > south.doubleValue())
      {
         final Double temp = north;
         north = south;
         south = temp;
      }

      if (west.doubleValue() > east.doubleValue())
      {
         final Double temp = east;
         east = west;
         west = temp;
      }

      Date minDate = minDateParam;
      Date maxDate = maxDateParam;

      if (minDate == null)
      {
         minDate = new Date(0);
      }
      if (maxDate == null)
      {
         maxDate = new Date();
      }

      if (minDate.getTime() > maxDate.getTime())
      {
         final Date temp = minDate;
         minDate = maxDate;
         maxDate = temp;
      }

      try (PooledEntityManager em = EntityManagerPool.borrowEntityManager())
      {
         final String countQuery = "select count(1) from TwitterStream t ";
         final String selectQuery = "select t from TwitterStream t ";
         final String where = "where t.lon > :minLon and t.lon < :maxLon "
               + "and t.lat > :minLat and t.lat < :maxLat and t.time > :minTime and t.time < :maxTime ";

         // first find out how many records we are going to get
         final TypedQuery<Long> qCount = em.createQuery(countQuery + where, Long.class);
         qCount.setParameter("minLon", west);
         qCount.setParameter("maxLon", east);
         qCount.setParameter("minLat", south);
         qCount.setParameter("maxLat", north);
         qCount.setParameter("minTime", minDate);
         qCount.setParameter("maxTime", maxDate);
         qCount.setMaxResults(1000);

         if (log.isDebugEnabled())
         {
            log.debug("count query = " + countQuery + where);
            log.debug("minLon = " + west + ", maxLon = " + east);
            log.debug("minLat =  = " + north + ", maxLat = " + south);
            log.debug("minTime = " + minDate + ", maxTime = " + maxDate);
         }
         final Long total = qCount.getSingleResult();

         log.debug("found " + total + " records");

         return Response.ok("not done").build();
      }
      catch (final Throwable t)
      {
         log.error("Cannot get the time range", t);
         return Response.serverError().entity("Times are currently unavailable").build();
      }
   }
}

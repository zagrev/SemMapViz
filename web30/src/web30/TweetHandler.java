/**
 * 
 */
package web30;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import web30.model.TwitterStream;
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
    * @param north
    * @param south
    * @param east
    * @param west
    * @param minDate
    * @param maxDate
    * @return the tweets for the given location and time
    */
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response getTweets(@QueryParam("north") final Double north, @QueryParam("south") final Double south,
         @QueryParam("east") final Double east, @QueryParam("west") final Double west,
         @QueryParam("mindate") final Date minDate, @QueryParam("maxdate") final Date maxDate)
   {
      log.debug("getTweets");
      final long startTime = System.currentTimeMillis();
      final HashMap<String, Object> params = new HashMap<>();

      String where = "where 1=1 ";

      // make sure all the parameters are set to something useful.
      // NOTE the db has lat and lon backwards
      if (north != null || south != null)
      {
         log.debug("t.lon between " + south + " and " + north);
         where += "and t.lon between :south and :north ";
         params.put("south", south == null ? Double.valueOf(-90.0) : south);
         params.put("north", north == null ? Double.valueOf(90.0) : north);
      }

      if (east != null || west != null)
      {
         where += "and t.lat between :west and :east ";
         params.put("east", east == null ? Double.valueOf(180.0) : east);
         params.put("west", west == null ? Double.valueOf(-180.0) : west);
      }

      if (minDate != null || maxDate != null)
      {
         where += "and t.time between :minTime and  :maxTime ";
         params.put("minTime", minDate == null ? new Date(0) : minDate);
         params.put("maxTime", maxDate == null ? new Date() : maxDate);
      }

      try (PooledEntityManager em = EntityManagerPool.borrowEntityManager())
      {
         // NOTE lat and lon are backward in this silly db
         final String countQuery = "select count(1) from TwitterStream t ";

         // first find out how many records we are going to get
         final TypedQuery<Long> qCount = em.createQuery(countQuery + where, Long.class);
         for (final String param : params.keySet())
         {
            qCount.setParameter(param, params.get(param));
         }

         if (log.isDebugEnabled())
         {
            log.debug("count query = " + countQuery + where);
            log.debug("minLon = " + west + ", maxLon = " + east);
            log.debug("minLat = " + south + ", maxLat = " + north);
            log.debug("minTime = " + minDate + ", maxTime = " + maxDate);
         }
         final Long total = qCount.getSingleResult();

         logTime("count", startTime);
         log.debug(String.format("found %,d records", total));

         final String selectQuery = "select t from TwitterStream t ";
         final TypedQuery<TwitterStream> qRetrieve = em.createQuery(selectQuery + where, TwitterStream.class);
         for (final String param : params.keySet())
         {
            qRetrieve.setParameter(param, params.get(param));
         }
         qRetrieve.setMaxResults(1000);

         final List<TwitterStream> list = qRetrieve.getResultList();

         final Tweets results = new Tweets();
         results.setOffset(0);
         results.setTotal(total.longValue());
         results.setTweet(list);

         return Response.ok(results).build();
      }
      catch (final Throwable t)
      {
         log.error("Cannot get the time range", t);
         return Response.serverError().entity("Times are currently unavailable").build();
      }
      finally
      {
         logTime("total", startTime);

      }
   }

   /**
    * @param label
    *           TODO
    * @param startTime
    */
   private void logTime(final String label, final long startTime)
   {
      final long endTime = System.currentTimeMillis();
      final double d = (endTime - startTime) / 1000.0;
      log.debug(String.format("%s time = %.2f seconds", label, Double.valueOf(d)));
   }
}

/**
 * 
 */
package web30;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import web30.persistence.EntityManagerPool;
import web30.persistence.PooledEntityManager;
import web30.util.ClientConnectionPool;
import web30.util.PooledClient;

/**
 * 
 */
public class TestTweetHandler
{
   /** the logger */
   private static Logger log = Logger.getLogger(TestTweetHandler.class);

   /** the service under test */
   private static Web30Service service;

   /**
    * stop the service under test
    */
   @AfterClass
   public static void afterClass()
   {
      service.stop();
   }

   /**
    * set up the service to test
    */
   @BeforeClass
   public static void beforeClass()
   {
      service = new Web30Service();
      service.start();

      // prime the database
      try (PooledEntityManager em = EntityManagerPool.borrowEntityManager())
      {
         log.debug("primed");
      }
   }

   /**
    * @throws Exception
    *            on error
    */
   @Test
   public void testGetTweetsBoundingBox() throws Exception
   {
      log.debug("testGetTweetsBoundingBox");
      try (final PooledClient client = ClientConnectionPool.borrowClient("web3"))
      {
         final WebTarget target = client.target(service.getUri()).path("tweet").queryParam("east", "0")
               .queryParam("west", "-122.0").queryParam("north", "37.0").queryParam("south", "32.0");
         log.debug("calling " + target.getUri());
         final Builder request = target.request();
         final Response response = request.get();

         assertNotNull(response);
         if (response.getStatus() != 200)
         {
            log.debug("received: " + response);
         }
         assertEquals(200, response.getStatus());

         final Tweets list = response.readEntity(Tweets.class);
         log.trace("got " + list);
         assertNotNull(list);

         log.debug("received: " + list.getTweet().size());
         assertTrue(list.getTotal() > 0);
         assertEquals(0, list.getOffset());
         assertNotNull(list.getTweet());
         assertEquals(1000, list.getTweet().size());
      }
   }

   /**
    * @throws Exception
    *            on error
    */
   @Test
   public void testGetTweetsInvalidTime() throws Exception
   {
      log.debug("testGetTweetsInvalidTime");
      try (final PooledClient client = ClientConnectionPool.borrowClient("web3"))
      {
         final WebTarget target = client.target(service.getUri()).path("tweet").queryParam("minTime", new Date());
         log.debug("calling " + target.getUri());
         final Builder request = target.request();
         final Response response = request.get();

         assertNotNull(response);
         if (response.getStatus() != 200)
         {
            log.debug("received: " + response);
         }
         assertEquals(200, response.getStatus());

         final Tweets list = response.readEntity(Tweets.class);
         log.trace("got " + list);
         assertNotNull(list);

         log.debug("received: " + list.getTweet().size());
         assertTrue(list.getTotal() > 0);
         assertEquals(0, list.getOffset());
         assertNotNull(list.getTweet());
         assertEquals(1000, list.getTweet().size());
      }
   }

   /**
    * @throws Exception
    *            on error
    */
   @Test
   public void testGetTweetsNoArgs() throws Exception
   {
      log.debug("testGetTweetsNoArgs");
      try (final PooledClient client = ClientConnectionPool.borrowClient("web3"))
      {
         final WebTarget target = client.target(service.getUri()).path("tweet");
         log.debug("calling " + target.getUri());
         final Response response = target.request().get();

         assertNotNull(response);
         if (response.getStatus() != 200)
         {
            log.debug("received: " + response);
         }
         assertEquals(200, response.getStatus());

         final Tweets list = response.readEntity(Tweets.class);
         log.trace("got " + list);
         assertNotNull(list);

         log.debug("received: " + list.getTweet().size());
         assertTrue(list.getTotal() > 0);
         assertEquals(0, list.getOffset());
         assertNotNull(list.getTweet());
         assertEquals(1000, list.getTweet().size());
      }
   }

   /**
    * @throws Exception
    */
   @Test
   public void testMapRange() throws Exception
   {
      log.debug("testMapRange");
      try (final PooledClient client = ClientConnectionPool.borrowClient("web3"))
      {
         final WebTarget target = client.target(service.getUri()).path("tweet").path("maprange");
         log.debug("calling " + target.getUri());
         final Response response = target.request().get();

         assertNotNull(response);
         if (response.getStatus() != 200)
         {
            log.debug("received: " + response);
         }

         assertEquals(200, response.getStatus());
         final BoundingBox answer = response.readEntity(BoundingBox.class);

         log.debug("received: " + answer);
         assertNotNull(answer);
         assertTrue("no east", answer.getEast() < 180.0);
         assertTrue("no east", answer.getEast() > -180.0);
         assertTrue("no west", answer.getWest() < 180.0);
         assertTrue("no west", answer.getWest() > -180.0);
         assertTrue("no north", answer.getNorth() < 90.0);
         assertTrue("no north", answer.getNorth() > -90.0);
         assertTrue("no south", answer.getSouth() < 90.0);
         assertTrue("no south", answer.getSouth() > -90.0);
      }
   }

   /**
    * @throws Exception
    */
   @Test
   public void testTimeRange() throws Exception
   {
      log.debug("testTimeRange");
      try (final PooledClient client = ClientConnectionPool.borrowClient("web3"))
      {
         final WebTarget target = client.target(service.getUri()).path("tweet").path("timerange");
         log.debug("calling " + target.getUri());
         final Response response = target.request().get();

         assertNotNull(response);
         if (response.getStatus() != 200)
         {
            log.debug("received: " + response);
         }
         assertEquals(200, response.getStatus());

         final TimeRange answer = response.readEntity(TimeRange.class);

         log.debug("received: " + answer);
         assertNotNull(answer);
         assertNotNull("no max time", answer.getMaxTime());
         assertTrue("no max time", answer.getMaxTime().getTime() > 0);
         assertNotNull("no min time", answer.getMinTime());
         assertTrue("no min time", answer.getMinTime().getTime() > 0);
      }
   }
}

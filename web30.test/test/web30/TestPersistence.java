/**
 * 
 */
package web30;

import org.junit.Test;

import web30.persistence.EntityManagerPool;
import web30.persistence.PooledEntityManager;

/**
 * 
 */
public class TestPersistence
{
   /**
    * verify that the entity manager successfully maps all the persisted objects
    */
   @Test
   public void testCreateEntityManager()
   {
      try (PooledEntityManager em = EntityManagerPool.borrowEntityManager())
      {
         // this should be good enough
      }
   }
}

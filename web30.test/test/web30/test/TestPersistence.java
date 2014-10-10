/**
 * 
 */
package web30.test;

import org.junit.Test;

import web30.persistence.EntityManagerPool;
import web30.persistence.PooledEntityManager;

/**
 * 
 */
public class TestPersistence
{
   @Test
   public void testCreateEntityManager()
   {
      try (PooledEntityManager em = EntityManagerPool.borrowEntityManager())
      {
         // this should be good enough
      }
   }
}

/**
 * 
 */
package web30;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The time range with a min and max DATE
 */
@XmlRootElement
public class TimeRange
{
   /** the earliest time a tweet is available */
   private Date minTime;
   /** the latest time a tweet is available */
   private Date maxTime;

   /**
    * Create a time range with the current date for min/max
    */
   public TimeRange()
   {
      this.minTime = new Date();
      this.maxTime = new Date();
   }

   /**
    * @param minTime
    * @param maxTime
    */
   public TimeRange(final Date minTime, final Date maxTime)
   {
      super();
      this.minTime = minTime;
      this.maxTime = maxTime;
   }

   /**
    * @return the maxTime
    */
   public Date getMaxTime()
   {
      return maxTime;
   }

   /**
    * @return the minTime
    */
   public Date getMinTime()
   {
      return minTime;
   }

   /**
    * @param maxTime
    *           the maxTime to set
    */
   public void setMaxTime(final Date maxTime)
   {
      this.maxTime = maxTime;
   }

   /**
    * @param minTime
    *           the minTime to set
    */
   public void setMinTime(final Date minTime)
   {
      this.minTime = minTime;
   }

}

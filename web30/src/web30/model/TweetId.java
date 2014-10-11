/**
 * 
 */
package web30.model;

import java.io.Serializable;
import java.util.Date;

/**
 * the composite key class
 */
public class TweetId implements Serializable
{
   /** the serial id */
   private static final long serialVersionUID = 1L;

   /** the tweet id */
   private String tweetid;

   /** the time */
   private Date time;

   /*
    * (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(final Object obj)
   {
      if (this == obj)
      {
         return true;
      }
      if (obj == null)
      {
         return false;
      }
      if (getClass() != obj.getClass())
      {
         return false;
      }
      final TweetId other = (TweetId) obj;
      if (time == null)
      {
         if (other.time != null)
         {
            return false;
         }
      }
      else if (!time.equals(other.time))
      {
         return false;
      }
      if (tweetid == null)
      {
         if (other.tweetid != null)
         {
            return false;
         }
      }
      else if (!tweetid.equals(other.tweetid))
      {
         return false;
      }
      return true;
   }

   /**
    * @return the time
    */
   public Date getTime()
   {
      return time;
   }

   /**
    * @return the tweetId
    */
   public String getTweetid()
   {
      return tweetid;
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + (time == null ? 0 : time.hashCode());
      result = prime * result + (tweetid == null ? 0 : tweetid.hashCode());
      return result;
   }

   /**
    * @param time
    *           the time to set
    */
   public void setTime(final Date time)
   {
      this.time = time;
   }

   /**
    * @param tweetId
    *           the tweetId to set
    */
   public void setTweetid(final String tweetId)
   {
      this.tweetid = tweetId;
   }
}

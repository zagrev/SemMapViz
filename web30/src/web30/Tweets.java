/**
 * 
 */
package web30;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import web30.model.TwitterStream;

/**
 * 
 */
@XmlRootElement
public class Tweets
{
   /** the offset of the first result in the collection */
   private long offset;
   /** the total number of tweets that could be returned */
   private long total;
   /** the tweets in the set of results */
   private List<TwitterStream> tweet;

   /**
    * @return the offset
    */
   public long getOffset()
   {
      return offset;
   }

   /**
    * @return the total
    */
   public long getTotal()
   {
      return total;
   }

   /**
    * @return the tweet
    */
   public List<TwitterStream> getTweet()
   {
      if (tweet == null)
      {
         tweet = new ArrayList<>();
      }
      return tweet;
   }

   /**
    * @param offset
    *           the offset to set
    */
   public void setOffset(final long offset)
   {
      this.offset = offset;
   }

   /**
    * @param total
    *           the total to set
    */
   public void setTotal(final long total)
   {
      this.total = total;
   }

   /**
    * @param tweet
    *           the tweet to set
    */
   public void setTweet(final List<TwitterStream> tweet)
   {
      this.tweet = tweet;
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      final StringBuilder b = new StringBuilder("Tweets [total=" + total + ", offset=" + offset + ", tweets = ");
      for (final TwitterStream curTweet : getTweet())
      {
         b.append("\n");
         b.append(curTweet.toString());
      }
      b.append("\n]");
      return b.toString();
   }
}

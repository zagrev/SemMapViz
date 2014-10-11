package web30.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQuery;

import org.hibernate.annotations.Immutable;

/**
 * The persistent class for the twitterstream database table.
 */
@Entity
@IdClass(TweetId.class)
@Immutable
@NamedQuery(name = "TwitterStream.findAll", query = "SELECT t FROM TwitterStream t")
public class TwitterStream implements Serializable
{
   /** the serial id */
   private static final long serialVersionUID = 1L;

   /** the bounding box */
   private String boundingbox;

   /** the latitude */
   private Double lat;

   /** the longitude */
   private Double lon;

   /** the tweet */
   private String text;

   /** the time of the tweet */
   @Id
   private Date time;

   /** the id of the tweet */
   @Id
   private String tweetid;

   /** the user that tweeted */
   private String userid;

   /**
    * create a tweet
    */
   public TwitterStream()
   {
   }

   /**
    * @return the bounding box
    */
   public String getBoundingbox()
   {
      return this.boundingbox;
   }

   /**
    * @return the latitude
    */
   public Double getLat()
   {
      return this.lat;
   }

   /**
    * @return the longitude
    */
   public Double getLon()
   {
      return this.lon;
   }

   /**
    * @return the message text
    */
   public String getText()
   {
      return this.text;
   }

   /**
    * @return the time
    */
   public Date getTime()
   {
      return this.time;
   }

   /**
    * @return the id
    */
   public String getTweetid()
   {
      return this.tweetid;
   }

   /**
    * @return the user
    */
   public String getUserid()
   {
      return this.userid;
   }

   /**
    * @param boundingbox
    */
   public void setBoundingbox(final String boundingbox)
   {
      this.boundingbox = boundingbox;
   }

   /**
    * @param lat
    */
   public void setLat(final Double lat)
   {
      this.lat = lat;
   }

   /**
    * @param lon
    */
   public void setLon(final Double lon)
   {
      this.lon = lon;
   }

   /**
    * @param text
    */
   public void setText(final String text)
   {
      this.text = text;
   }

   /**
    * @param time
    */
   public void setTime(final Date time)
   {
      this.time = time;
   }

   /**
    * @param tweetid
    */
   public void setTweetid(final String tweetid)
   {
      this.tweetid = tweetid;
   }

   /**
    * @param userid
    */
   public void setUserid(final String userid)
   {
      this.userid = userid;
   }

}

package web30.model;

import java.io.Serializable;

import javax.persistence.Column;
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
   private String lat;

   /** the longitude */
   @Column(name = "long")
   private String lon;

   /** the tweet */
   private String text;

   /** the time of the tweet */
   @Id
   private String time;

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
   public String getLat()
   {
      return this.lat;
   }

   /**
    * @return the longitude
    */
   public String getLon()
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
   public String getTime()
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
   public void setLat(final String lat)
   {
      this.lat = lat;
   }

   /**
    * @param long_
    */
   public void setLon(final String long_)
   {
      this.lon = long_;
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
   public void setTime(final String time)
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

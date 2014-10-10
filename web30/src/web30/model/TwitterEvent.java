package web30.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

/**
 * The persistent class for the twitterevents database table.
 */
@Entity
@Table(name = "twitterevents")
@Immutable
@NamedQuery(name = "TwitterEvent.findAll", query = "SELECT t FROM TwitterEvent t")
public class TwitterEvent implements Serializable
{
   /** the serial id */
   private static final long serialVersionUID = 1L;

   /** the end time of this event */
   @Column(name = "end_time")
   private Timestamp endTime;

   /** the type of this event */
   @Column(name = "event_type")
   private String eventType;

   /** the geo hash number of this event */
   @Column(name = "geo_hash_number")
   private int geoHashNumber;

   /** the color of this event */
   @Column(name = "icon_color")
   private String iconColor;

   /** the id */
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private int id;

   /** the impact */
   private int impact;

   /** the latitude */
   private String lat;

   /** the longitude */
   private String longitue;

   /** the event start time */
   @Column(name = "start_time")
   private Timestamp startTime;

   /**
    * create an event
    */
   public TwitterEvent()
   {
   }

   /**
    * @return the time this event ended
    */
   public Timestamp getEndTime()
   {
      return this.endTime;
   }

   /**
    * @return the type of this event
    */
   public String getEventType()
   {
      return this.eventType;
   }

   /**
    * @return the hash number
    */
   public int getGeoHashNumber()
   {
      return this.geoHashNumber;
   }

   /**
    * @return the icon color
    */
   public String getIconColor()
   {
      return this.iconColor;
   }

   /**
    * @return the id
    */
   public int getId()
   {
      return this.id;
   }

   /**
    * @return the impact
    */
   public int getImpact()
   {
      return this.impact;
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
   public String getLongitue()
   {
      return this.longitue;
   }

   /**
    * @return the start time of this event
    */
   public Timestamp getStartTime()
   {
      return this.startTime;
   }

   /**
    * @param endTime
    */
   public void setEndTime(final Timestamp endTime)
   {
      this.endTime = endTime;
   }

   /**
    * @param eventType
    */
   public void setEventType(final String eventType)
   {
      this.eventType = eventType;
   }

   /**
    * @param geoHashNumber
    */
   public void setGeoHashNumber(final int geoHashNumber)
   {
      this.geoHashNumber = geoHashNumber;
   }

   /**
    * @param iconColor
    */
   public void setIconColor(final String iconColor)
   {
      this.iconColor = iconColor;
   }

   /**
    * @param id
    */
   public void setId(final int id)
   {
      this.id = id;
   }

   /**
    * @param impact
    */
   public void setImpact(final int impact)
   {
      this.impact = impact;
   }

   /**
    * @param lat
    */
   public void setLat(final String lat)
   {
      this.lat = lat;
   }

   /**
    * @param longitue
    */
   public void setLongitue(final String longitue)
   {
      this.longitue = longitue;
   }

   /**
    * @param startTime
    */
   public void setStartTime(final Timestamp startTime)
   {
      this.startTime = startTime;
   }

}

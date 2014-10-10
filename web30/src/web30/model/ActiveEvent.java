package web30.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

/**
 * The persistent class for the activeevents database table.
 */
@Entity
@Table(name = "activeevents")
@Immutable
@NamedQuery(name = "ActiveEvent.findAll", query = "SELECT a FROM ActiveEvent a")
public class ActiveEvent implements Serializable
{
   /** The serial ID */
   private static final long serialVersionUID = 1L;

   /** the event duration */
   private int duration;

   /**
    * the ID
    */
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private String eventid;

   /** the latitude of the event */
   private double eventlat;

   /** The longitude of the event */
   private double eventlong;

   /** Get the time of the event */
   private String eventtime;

   /** the type of the event */
   private String eventtype;

   /**
    * Create an event
    */
   public ActiveEvent()
   {
   }

   /**
    * get the duration of the event
    * 
    * @return the duration
    */
   public int getDuration()
   {
      return this.duration;
   }

   /**
    * @return the id
    */
   public String getEventid()
   {
      return this.eventid;
   }

   /**
    * @return the latitude
    */
   public double getEventlat()
   {
      return this.eventlat;
   }

   /**
    * @return the longitude
    */
   public double getEventlong()
   {
      return this.eventlong;
   }

   /**
    * @return the time
    */
   public String getEventtime()
   {
      return this.eventtime;
   }

   /**
    * @return the type
    */
   public String getEventtype()
   {
      return this.eventtype;
   }

   /**
    * @param duration
    */
   public void setDuration(final int duration)
   {
      this.duration = duration;
   }

   /**
    * @param eventid
    */
   public void setEventid(final String eventid)
   {
      this.eventid = eventid;
   }

   /**
    * @param eventlat
    */
   public void setEventlat(final double eventlat)
   {
      this.eventlat = eventlat;
   }

   /**
    * @param eventlong
    */
   public void setEventlong(final double eventlong)
   {
      this.eventlong = eventlong;
   }

   /**
    * @param eventtime
    */
   public void setEventtime(final String eventtime)
   {
      this.eventtime = eventtime;
   }

   /**
    * @param eventtype
    */
   public void setEventtype(final String eventtype)
   {
      this.eventtype = eventtype;
   }

}

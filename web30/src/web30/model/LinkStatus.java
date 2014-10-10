package web30.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Immutable;

/**
 * The persistent class for the linkstatus database table.
 */
@Entity
@Immutable
@NamedQuery(name = "LinkStatus.findAll", query = "SELECT l FROM LinkStatus l")
public class LinkStatus implements Serializable
{
   /** the serial version */
   private static final long serialVersionUID = 1L;
   /** the delay on this link at this time */
   private int linkdelay;

   // bi-directional many-to-one association to LinkDescription
   /**
    * the description for this link
    */
   @ManyToOne
   @JoinColumn(name = "linkid")
   private LinkDescription linkdescription;

   /** the amount of traffic on this link */
   private int linkoccupancy;
   /** the speed of the traffic on this link */
   private int linkspeed;
   /** the time it takes to travel this link */
   private int linktraveltime;
   /** the volume of traffic on this link */
   private int linkvolume;

   /** the date and time of the status */
   @Id
   @Temporal(TemporalType.TIMESTAMP)
   private Date timestamp;

   /**
    * create a status
    */
   public LinkStatus()
   {
   }

   /**
    * @return get the delay on this link
    */
   public int getLinkdelay()
   {
      return this.linkdelay;
   }

   /**
    * @return get the description of this link
    */
   public LinkDescription getLinkdescription()
   {
      return this.linkdescription;
   }

   /**
    * @return get the occupancy of this link
    */
   public int getLinkoccupancy()
   {
      return this.linkoccupancy;
   }

   /**
    * @return get the speed of this link
    */
   public int getLinkspeed()
   {
      return this.linkspeed;
   }

   /**
    * @return get the travel time for this link
    */
   public int getLinktraveltime()
   {
      return this.linktraveltime;
   }

   /**
    * @return get the volume of this link
    */
   public int getLinkvolume()
   {
      return this.linkvolume;
   }

   /**
    * @return get the date and time of this status
    */
   public Date getTimestamp()
   {
      return this.timestamp;
   }

   /**
    * @param linkdelay
    */
   public void setLinkdelay(final int linkdelay)
   {
      this.linkdelay = linkdelay;
   }

   /**
    * @param linkdescription
    */
   public void setLinkdescription(final LinkDescription linkdescription)
   {
      this.linkdescription = linkdescription;
   }

   /**
    * @param linkoccupancy
    */
   public void setLinkoccupancy(final int linkoccupancy)
   {
      this.linkoccupancy = linkoccupancy;
   }

   /**
    * @param linkspeed
    */
   public void setLinkspeed(final int linkspeed)
   {
      this.linkspeed = linkspeed;
   }

   /**
    * @param linktraveltime
    */
   public void setLinktraveltime(final int linktraveltime)
   {
      this.linktraveltime = linktraveltime;
   }

   /**
    * @param linkvolume
    */
   public void setLinkvolume(final int linkvolume)
   {
      this.linkvolume = linkvolume;
   }

   /**
    * @param timestamp
    */
   public void setTimestamp(final Date timestamp)
   {
      this.timestamp = timestamp;
   }

}

package web30.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Immutable;

/**
 * The persistent class for the linklocation database table.
 */
@Entity
@Immutable
@NamedQuery(name = "LinkLocation.findAll", query = "SELECT l FROM LinkLocation l")
public class LinkLocation implements Serializable
{
   /** the serial id */
   private static final long serialVersionUID = 1L;

   /** the ending latitude */
   private double endlat;
   /** the ending longitude */
   private double endlong;

   // bi-directional one-to-one association to LinkDescription
   /** the descriptions for this link */
   @OneToOne
   @JoinColumn(name = "linkid")
   private LinkDescription linkdescription;

   /** the id for this link */
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private int linkid;

   /** the starting latitude */
   private double startlat;
   /** the starting longitude */
   private double startlong;

   /**
    * create a link location
    */
   public LinkLocation()
   {
   }

   /**
    * @return the ending latitude
    */
   public double getEndlat()
   {
      return this.endlat;
   }

   /**
    * @return the ending longitude
    */
   public double getEndlong()
   {
      return this.endlong;
   }

   /**
    * @return the link description
    */
   public LinkDescription getLinkdescription()
   {
      return this.linkdescription;
   }

   /**
    * @return the id
    */
   public int getLinkid()
   {
      return this.linkid;
   }

   /**
    * @return the starting latitude
    */
   public double getStartlat()
   {
      return this.startlat;
   }

   /**
    * @return the starting longitude
    */
   public double getStartlong()
   {
      return this.startlong;
   }

   /**
    * @param endlat
    */
   public void setEndlat(final double endlat)
   {
      this.endlat = endlat;
   }

   /**
    * @param endlong
    */
   public void setEndlong(final double endlong)
   {
      this.endlong = endlong;
   }

   /**
    * @param linkdescription
    */
   public void setLinkdescription(final LinkDescription linkdescription)
   {
      this.linkdescription = linkdescription;
   }

   /**
    * @param linkid
    */
   public void setLinkid(final int linkid)
   {
      this.linkid = linkid;
   }

   /**
    * @param startlat
    */
   public void setStartlat(final double startlat)
   {
      this.startlat = startlat;
   }

   /**
    * @param startlong
    */
   public void setStartlong(final double startlong)
   {
      this.startlong = startlong;
   }

}

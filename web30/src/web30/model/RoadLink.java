package web30.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

/**
 * The persistent class for the roadlinks database table.
 */
@Entity
@Table(name = "roadlinks")
@Immutable
@NamedQuery(name = "RoadLink.findAll", query = "SELECT r FROM RoadLink r")
public class RoadLink implements Serializable
{
   /** the serial id */
   private static final long serialVersionUID = 1L;

   /** the link description */
   @ManyToOne
   @JoinColumn(name = "roadid")
   private LinkDescription linkdescription;

   /** the link id */
   @Id
   private int linkid;

   /**
    * 
    */
   public RoadLink()
   {
   }

   /**
    * @return the description
    */
   public LinkDescription getLinkdescription()
   {
      return this.linkdescription;
   }

   /**
    * @return the link id
    */
   public int getLinkid()
   {
      return this.linkid;
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

}

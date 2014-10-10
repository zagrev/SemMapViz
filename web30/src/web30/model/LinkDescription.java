package web30.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Immutable;

/**
 * The persistent class for the linkdescription database table.
 */
@Entity
@Immutable
@NamedQuery(name = "LinkDescription.findAll", query = "SELECT l FROM LinkDescription l")
public class LinkDescription implements Serializable
{
   /** the serial id */
   private static final long serialVersionUID = 1L;
   /** the street the link is from */
   private String fromstreet;

   /** the id */
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private int linkid;

   // bi-directional one-to-one association to LinkLocation
   /** the link location */
   @OneToOne(mappedBy = "linkdescription")
   private LinkLocation linklocation;

   // bi-directional many-to-one association to LinkStatus
   /** the statuses for this link */
   @OneToMany(mappedBy = "linkdescription")
   private List<LinkStatus> linkstatuses;

   /** the street we are describing */
   private String onstreet;

   // bi-directional many-to-one association to RoadLink
   /** the roads for this link */
   @OneToMany(mappedBy = "linkdescription")
   private List<RoadLink> roadlinks;

   /** the speed limit */
   private int speedlimit;

   /** the street this link goes to */
   private String tostreet;

   /**
    * create a description
    */
   public LinkDescription()
   {
   }

   /**
    * @param linkstatus
    * @return the added link status
    */
   public LinkStatus addLinkstatus(final LinkStatus linkstatus)
   {
      getLinkstatuses().add(linkstatus);
      linkstatus.setLinkdescription(this);

      return linkstatus;
   }

   /**
    * @param roadlink
    * @return the added road link
    */
   public RoadLink addRoadlink(final RoadLink roadlink)
   {
      getRoadlinks().add(roadlink);
      roadlink.setLinkdescription(this);

      return roadlink;
   }

   /**
    * @return the from street
    */
   public String getFromstreet()
   {
      return this.fromstreet;
   }

   /**
    * @return the id
    */
   public int getLinkid()
   {
      return this.linkid;
   }

   /**
    * @return the location
    */
   public LinkLocation getLinklocation()
   {
      return this.linklocation;
   }

   /**
    * @return the statuses
    */
   public List<LinkStatus> getLinkstatuses()
   {
      return this.linkstatuses;
   }

   /**
    * @return the street this link is on
    */
   public String getOnstreet()
   {
      return this.onstreet;
   }

   /**
    * @return the road for this link
    */
   public List<RoadLink> getRoadlinks()
   {
      return this.roadlinks;
   }

   /**
    * @return the speed limit
    */
   public int getSpeedlimit()
   {
      return this.speedlimit;
   }

   /**
    * @return the to street
    */
   public String getTostreet()
   {
      return this.tostreet;
   }

   /**
    * @param linkstatus
    * @return the status removed
    */
   public LinkStatus removeLinkstatus(final LinkStatus linkstatus)
   {
      getLinkstatuses().remove(linkstatus);
      linkstatus.setLinkdescription(null);

      return linkstatus;
   }

   /**
    * @param roadlink
    * @return the road removed
    */
   public RoadLink removeRoadlink(final RoadLink roadlink)
   {
      getRoadlinks().remove(roadlink);
      roadlink.setLinkdescription(null);

      return roadlink;
   }

   /**
    * @param fromstreet
    */
   public void setFromstreet(final String fromstreet)
   {
      this.fromstreet = fromstreet;
   }

   /**
    * @param linkid
    */
   public void setLinkid(final int linkid)
   {
      this.linkid = linkid;
   }

   /**
    * @param linklocation
    */
   public void setLinklocation(final LinkLocation linklocation)
   {
      this.linklocation = linklocation;
   }

   /**
    * @param linkstatuses
    */
   public void setLinkstatuses(final List<LinkStatus> linkstatuses)
   {
      this.linkstatuses = linkstatuses;
   }

   /**
    * @param onstreet
    */
   public void setOnstreet(final String onstreet)
   {
      this.onstreet = onstreet;
   }

   /**
    * @param roadlinks
    */
   public void setRoadlinks(final List<RoadLink> roadlinks)
   {
      this.roadlinks = roadlinks;
   }

   /**
    * @param speedlimit
    */
   public void setSpeedlimit(final int speedlimit)
   {
      this.speedlimit = speedlimit;
   }

   /**
    * @param tostreet
    */
   public void setTostreet(final String tostreet)
   {
      this.tostreet = tostreet;
   }

}

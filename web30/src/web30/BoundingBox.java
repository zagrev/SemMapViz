/**
 * 
 */
package web30;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 */
@XmlRootElement
public class BoundingBox
{
   /** the top of the box */
   private double north;
   /** the right side of the box */
   private double east;
   /** the left side of the box */
   private double west;
   /** the bottom of the box */
   private double south;

   /**
    * create an bounding box that wraps the world
    */
   public BoundingBox()
   {
   }

   /**
    * @param north
    * @param east
    * @param west
    * @param south
    */
   public BoundingBox(final double north, final double east, final double west, final double south)
   {
      this.north = north;
      this.east = east;
      this.west = west;
      this.south = south;
   }

   /**
    * @return the east
    */
   public double getEast()
   {
      return east;
   }

   /**
    * @return the north
    */
   public double getNorth()
   {
      return north;
   }

   /**
    * @return the south
    */
   public double getSouth()
   {
      return south;
   }

   /**
    * @return the west
    */
   public double getWest()
   {
      return west;
   }

   /**
    * @param east
    *           the east to set
    */
   public void setEast(final double east)
   {
      this.east = east;
   }

   /**
    * @param north
    *           the north to set
    */
   public void setNorth(final double north)
   {
      this.north = north;
   }

   /**
    * @param south
    *           the south to set
    */
   public void setSouth(final double south)
   {
      this.south = south;
   }

   /**
    * @param west
    *           the west to set
    */
   public void setWest(final double west)
   {
      this.west = west;
   }
}

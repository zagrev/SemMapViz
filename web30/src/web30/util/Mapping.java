package web30.util;
/**
 *
 */


/**
 * maps an extension to a mime type. these area created from the mime type list, normally stored in mime-types.xml,
 */
public class Mapping
{
   /** the file extension for the mime-type */
   private String extension;
   /** the mime type */
   private String mimeType;

   /**
    * @return the extension
    */
   public String getExtension()
   {
      return extension;
   }

   /**
    * @return the mimeType
    */
   public String getMimeType()
   {
      return mimeType;
   }

   /**
    * @param extension
    *           the extension to set
    */
   public void setExtension(final String extension)
   {
      this.extension = extension;
   }

   /**
    * @param mimeType
    *           the mimeType to set
    */
   public void setMimeType(final String mimeType)
   {
      this.mimeType = mimeType;
   }
}

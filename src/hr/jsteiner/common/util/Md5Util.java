package hr.jsteiner.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {
  private static final char[] hexChars =
    { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

  private static String toHexString(byte[] bytearray) {
      StringBuilder sb = new StringBuilder(); 
      for( byte b: bytearray ) {
              sb.append( hexChars[ b >> 4 & 0x0F ] );
              sb.append( hexChars[ b & 0x0F ] );
      }
      return sb.toString();
  }
  
  private static MessageDigest md = null;
  
  private static byte[] hash(byte[] dataToHash){
      if( md == null )
              try{
                      md = MessageDigest.getInstance("MD5");
              }catch( NoSuchAlgorithmException ignorada ){
              }
      return md.digest(dataToHash); 
  }
  
  public static String generateHash(String stringToHash){
      return toHexString( hash(stringToHash.getBytes()) );
  }

}

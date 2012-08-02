package hr.jsteiner.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
  
  /**
   * Extracts regex match from string
   * @param string to search
   * @param pattern regex pattern
   * @return matched string, or null if nothing found (or null if any of the parameters is null)
   */
  public static String regexExtract(String string, String pattern) {
    if (string == null || pattern == null) {
      return null;
    }
    
    Matcher m = regexGetMatcherForPattern(string, pattern);
    if (m == null) {
      return null;
    }
    
    if (m.find()) {
      return m.group(0);
    }
    else return null;
  }
  
  public static Matcher regexGetMatcherForPattern(String string, String pattern) {
    if (string == null || pattern == null) {
      return null;
    }
    
    /**
     * strip newline characters because it doesn't work well with this.
     */
    string = string.replace("\n", "");
    
    Pattern p = Pattern.compile(pattern);
    Matcher m = p.matcher(string);
    
    return m;
  }
  
  /**
   * 
   * @param path to extract the file or folder name from
   * @return null if not found or if path is null
   */
  public static String extractFolderOrFilename(String path) {
    return regexExtract(path, "[^/]*$");
  }
  
  /**
   * 
   * @param xml
   * @param openTag - for example openTag="<a>"
   * @param closeTag - for example closeTag="</a>"
   * @return
   */
  public static String getValueInsideXmlTags(String xml, String openTag, String closeTag) {
    String regexCode = "(?<=" + openTag + ")(.*?)(?=" + closeTag + ")";
    
    return regexExtract(xml, regexCode);
  }
  
  /**
   * @param xml
   * @param tag - without brackets, tag="html" will mean give me everything between <html> and
   * </html>.
   * @return
   */
  public static String getValueInsideXmlTags(String xml, String tag) {
    //String regexCode = "(?<=<" + tag + ">)(.*?)(?=</" + tag + ">)";
    //regexCode.replace("tag", tag);
    
    
    //return regexExtract(xml, regexCode);
    return getValueInsideXmlTags(xml, "<" + tag + ">", "</" + tag + ">");
  }
  
  public static Matcher getMatcherForXmlTags(String xml, String tag) {
    String regexCode = "(?<=<" + tag + ">)(.*?)(?=</" + tag + ">)";
    Matcher m = regexGetMatcherForPattern(xml, regexCode);
    return m;
  }
  
  public static String milisecondsToMinutesAndSeconds(int milliseconds) {
    int seconds = (int) (milliseconds / 1000) % 60 ;
    int minutes = (int) ((milliseconds / (1000*60)) % 60);
    int hours   = (int) ((milliseconds / (1000*60*60)) % 24);

    StringBuffer buffer = new StringBuffer();
    String prefix = "";
    if (hours > 0) {
      prefix = hours < 10 ? "0" : "";
        buffer.append(prefix + hours + ":");
    }
    
    prefix = minutes < 10 ? "0" : "";
    buffer.append(prefix + minutes + ":");
    
    prefix = seconds < 10 ? "0" : "";
    buffer.append(prefix + seconds);
    
    return buffer.toString();
  }
}

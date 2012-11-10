package hr.jsteiner.common.util;

import hr.jsteiner.common.domain.xml.XmlTag;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class XmlUtil {
  
  public static final String CDATA_OPEN_TAG = "<![CDATA[";
  public static final String CDATA_CLOSE_TAG = "]]>";
  
  /**
   * This is a list of tags to ignore. It's useful to put elements that usually do not have a
   * closing element, like horizontal rule. This element will be then treated as an empty
   * (self-closed) tag.
   */
  public static final String[] ignoredTags = {"<hr>"};
  
  public static boolean isTagIgnored(String tag) {
    for (String ignoredTag : ignoredTags) {
      if (ignoredTag.equals(tag)) {
        return true;
      }
    }
    return false;
  }
  
  public static Matcher getMatcher(String xml, String regex) {
    if (xml == null || regex == null) {
      return null;
    }
    
    Pattern p = Pattern.compile(regex, Pattern.DOTALL | Pattern.MULTILINE);
    Matcher m = p.matcher(xml);
    return m;
  }
  
  /**
   * Extracts all attributes from tags, for example <tag attr1="value1" attr2="value2">
   * @param tag
   * @return null if nothing found, {@link HashMap} with attributeName-attributeValue pairs.
   */
  public static Map<String, String> extractAttributesFromTag(String tag) {
    if (tag == null) {
      return null;
    }
    
    /*
     * extract attribute=value pairs from tag 
     */
    Matcher m = getMatcher(tag, " .*(?=[>])");
    
    if (m.find() == false) {
      return null;
    }
      
    Map<String, String> attributes = new HashMap<String, String>();
    
    String match = m.group();
    if (match == null) {
      return null;
    }
    
    int attrStart = 0;
    /*
     * Match attribute keys (between ' ' and '=' characters). 
     */
    Matcher mAttrName = getMatcher(match, "(?<= ).*?(?==)");
    while (mAttrName.find(attrStart)) {
      String attrName = mAttrName.group().trim();
      String attrValue = null;
     
      /*
       * Look for attribute value after the found attribute key.
       */
      int indexForValueStart = mAttrName.end();
      
      /*
       * Match attribute values (between '="' (equals followed by quote) and '"' (quote)).  
       */
      Matcher mAttrValue = getMatcher(match, "(?<=\").*?(?=\")");
      if (mAttrValue.find(indexForValueStart)) {
        attrValue = mAttrValue.group();
        
        /*
         * Set the search for next attribute key-value pair after this (last found) attribute value.
         */
        attrStart = mAttrValue.end();
        
        /*
         * Save the found attribute to list.
         */
        attributes.put(attrName, attrValue);

        continue;
      }
      
      /** SHOULD NOT REACH HERE! **/
      break;
    }
      
    return attributes;
  }
  
  /**
   * Extract the first level (top hierarchy) tags from the xml string. The children of these tags
   * are in {@link XmlTag} mChildren variable. The attributes of this tags are in the mAttributes
   * {@link HashMap}.
   * @param xml to parse
   * @return null if nothing parsed, or {@link HashMap} with top level {@link XmlTag}s.
   * @throws ParseException
   */
  public static List<XmlTag> extractFirstLevel(String xml) throws ParseException {
    if (xml == null) {
      return null;
    }
    
    if (xml.trim().startsWith(CDATA_OPEN_TAG) && xml.trim().endsWith(CDATA_CLOSE_TAG)) {
      /*
       *  So that it can be added as a value and not as a child while in recursive call.
       */
      return null;
    }
    
    
    Matcher m = getMatcher(xml, "<[^/?!].*?>");
    
    if (m == null) {
      return null;
    }
    
    /*
     * List to store the tags found
     */
    List<XmlTag> firstLevelXmlTags = new ArrayList<XmlTag>();
    
    int index = 0;
    /*
     * Start search from the beggining of the string. After the extracted Tag, the index
     * will be moved to position after the closing tag.
     */
    while (m.find(index)) {
      String tag = m.group();
      
      String tagName = null;
      if (tag.indexOf(' ') < 0) {
        tagName = tag.substring(1, tag.length() - 1);
      }
      else {
        tagName = tag.substring(1, tag.indexOf(' '));
      } 
      
      /*
       * just in case 
       */
      if (tagName == null) {
        /** should never get here **/
        throw new ParseException("should never reach here!", index);
      }
        
      tagName = tagName.trim();
      if (tagName.endsWith("/")) {
        try {
          tagName = tagName.substring(0, tagName.length() - 1);
        }
        catch (IndexOutOfBoundsException e) {
          // do nothing
        }
      }
      
      XmlTag xmlTag = new XmlTag(tagName);
      Map<String, String> attributes = extractAttributesFromTag(tag);
      xmlTag.setAttributes(attributes);
      
      firstLevelXmlTags.add(xmlTag);
      
      /** 
       * if it's empty element tag for example
       * <tag attribute="value" />
       */
      int start = m.end();
      if (tag.endsWith("/>") || isTagIgnored(tag)) {
        /**
         * do not look for a closing tag because it's an empty element 
         */
        index = start;
        continue;
      }
      
      /*
       * else look for a closing tag from this position
       */
      Matcher mNextOpenOrClose = getMatcher(xml, "</?" + tagName + "[ ]{0,}>");
      
      
      int end = -1;
      
      /*
       * match the last occurrence -- for example if there are nested tags of the same type
       * like <artist><similar><artist>...</artist><artist>...</artist></similar></artist>
       */
      int startFrom = m.end();
      int counter = 0;
      while(mNextOpenOrClose.find(startFrom)) {
        /*
         * look for the next tag after the closing element
         */
        String nextOpenOrClose = mNextOpenOrClose.group();
        
        if (nextOpenOrClose.startsWith("</")) {
          /** closed tag **/
          if (counter == 0) {
            end = mNextOpenOrClose.start();
            index = mNextOpenOrClose.end();
            break;
          }
          counter--;
        }
        else {
          /** new nested tag **/
          counter++;
        }
        
        /** for next iteration **/
        startFrom = mNextOpenOrClose.end();
      }
      
      /*
       *  This should always be true, because every tag which isn't an empty tag
       *  should have it's closing tag. If it doesn't it could mean that the XML isn't valid. 
       */
      if (end != -1) {
        
        /** the text between the opening and the closing tag **/
        try {
          String childrenXml = xml.substring(start, end);
          /*
           * recursive call to this method. 
           */
          List<XmlTag> children = extractFirstLevel(childrenXml);
          
          /*
           * If xmlTag is null this means that no children was found - set the value to xmlTag
           * TODO if there is a situation that the element has both children as xml tags and 
           * children as text, it will only take children here. Fix pending!
           */
          if (children != null) {
            xmlTag.setChildren(children);
          }
          else {
            childrenXml = childrenXml.trim();
            /*
             * this is to address the todo issue above. 
             */
            xmlTag.setChildrenRaw(childrenXml);
            if (childrenXml.startsWith(CDATA_OPEN_TAG) && childrenXml.endsWith(CDATA_CLOSE_TAG)) {
              try {
                childrenXml = childrenXml.substring(
                    CDATA_OPEN_TAG.length(), childrenXml.length() - CDATA_CLOSE_TAG.length());
              }
              catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
              }
            }
            xmlTag.setValue(childrenXml);
          }
        }
        catch (IndexOutOfBoundsException e) {
          e.printStackTrace();
        }
        continue;
      }
      
      
      /*
       *  SHOULD NOT REACH HERE 
       */
      throw new ParseException("should never reach here!", index);
    }
    
    if (firstLevelXmlTags.size() == 0) {
      return null;
    }
    
    return firstLevelXmlTags;
  }
  
  public static XmlTag parseXml(String xml) {
    if (xml == null || "".equals(xml)) {
      return null;
    }
    
    List<XmlTag> xmlTags = null;
    try {
      xmlTags = extractFirstLevel(xml);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    
    if (xmlTags != null && xmlTags.size() > 0 ) {
      return xmlTags.get(0);
    }
    
    return null;
  }
  
  /**
   * Recursively search the {@link XmlTag} for a child with specified name. If attributeKey and 
   * attributeValue are not null, it will search until both name and attribute key-value pairs 
   * match.
   * @param xmlTag to start the search from
   * @param name element name to search
   * @param attributeKey 
   * @param attributeValue
   * @return {@link XmlTag} if found, null if not found
   */
  public static XmlTag findByName(final XmlTag xmlTag, final String name,
      final String attributeKey, final String attributeValue) {
    if (xmlTag == null || name == null) {
      return null;
    }
    
    /*
     * If element name matches
     */
    if (name.equals(xmlTag.getName())) {
      
      /*
       * If any of these two parameters is null, do not search by attributes
       */
      if (attributeKey == null || attributeValue == null) {
        return xmlTag;
      }
      
      /*
       * Else search attribute key-value pairs
       */
      if (xmlTag.getAttributes() != null) {
        Iterator<Entry<String, String>> it = xmlTag.getAttributes().entrySet().iterator();
        while(it.hasNext()) {
          Entry<String, String> entry = it.next();
          if (attributeKey.equals(entry.getKey()) && attributeValue.equals(entry.getValue())) {
            return xmlTag;
          }
        }
      }
      
    }
    
    /*
     * If not found, search elements children recursively 
     */
    if (xmlTag.getChildren() != null) {
      for (XmlTag child : xmlTag.getChildren()) {
        if (child != null) {
          XmlTag found = findByName(child, name, attributeKey, attributeValue);
          if (found != null) {
            return found;
          }
        }
      }
    }
    
    /*
     * If nothing found go one step up in recursion
     */
    return null;
  }
  
  /**
   * Recursively search for a child of xmlTag with specific name. 
   * See {@link #findByName(XmlTag, String, String, String)} 
   * @param xmlTag
   * @param name
   * @return
   */
  public static XmlTag findByName(XmlTag xmlTag, String name) {
    return findByName(xmlTag, name, null, null);
  }
}

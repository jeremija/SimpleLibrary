package hr.jsteiner.common.domain.xml;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class XmlTag {
  protected String mName = null;
  protected String mChildrenRaw = null;
  protected List<XmlTag> mChildren = null;
  protected Map<String, String> mAttributes = null;
  protected String mValue = null;
  
  int mLastIndex = -1;
  
  /**
   * Main constructor 
   * @param name
   * @throws #{@link IllegalArgumentException} if name is null or empty string
   */
  public XmlTag(String name) {
    if (name == null || "".equals(name)) {
      throw new IllegalArgumentException("name must not be null or empty string");
    }
    mName = name;
  }
  
  /** CHILDREN UTIL (GETTERS) **/
  
  /**
   * Always gets the first {@link #XmlTag(String)} (starts the search from index 0).
   * @param tagName
   * @return {@link #XmlTag(String)} if found, null otherwise.
   */
  public XmlTag getChildByName(String tagName) {
    int index = getChildIndexByName(tagName, 0);
    return getChildByIndex(index);
  }
  
  /**
   * to avoid nullpointer exceptions
   * @param tagName
   * @return
   */
  public String getChildByNameValue(String tagName) {
    XmlTag child = getChildByName(tagName);
    if (child == null) {
      return null;
    }
    return child.getValue();
  }
  
  /**
   * Gets the first {@link XmlTag} from {@link #mChildren} and memorizes the last index to 
   * {@link #mLastIndex}. The next time this method is called, it will search from 
   * <code>mLastIndex++</code>. This is useful when fetching duplicates. If you wish to reset the
   * counter, call {@link #resetLastIndex()}
   * @param tagName
   * @return
   */
  public XmlTag getChildByNameAvoidDuplicates(String tagName) {
    mLastIndex++;
    int index = getChildIndexByName(tagName, mLastIndex);
    return getChildByIndex(index);
  }
  
  /**
   * 
   * @return the next {@link #XmlTag(String)} in {@link #mChildren}. Also memorizes the used index
   * to {@link #mLastIndex} like {@link #getChildByNameAvoidDuplicates(String)}
   */
  public XmlTag getNextChildInOrder() {
    mLastIndex++;
    return getChildByIndex(mLastIndex);
  }
  
  /** 
   * @param index
   * @return {@link XmlTag} if found, null if not found or index out of bounds.
   */
  public XmlTag getChildByIndex(int index) {
    if (index == -1 || index > getChildren().size() - 1) {
      return null;
    }
    return getChildren().get(index);
  }
  
  /**
   * Returns the first index of {@link XmlTag} with name==tagName from startIndex in 
   * {@link #mChildren} which has the specified tagName.
   * @param tagName
   * @param startIndex
   * @return position if found, -1 if not found
   */
  public int getChildIndexByName(String tagName, int startIndex) {
    if (getChildren() == null || tagName == null || "".equals(tagName) ) {
      return -1;
    }
    for(int i = startIndex; i < getChildren().size(); i++) {
      XmlTag listEntry = getChildren().get(i);
      if (new XmlTag(tagName).equals(listEntry)) {
        return i;
      }
    }
    
    /** if nothing found **/
    return -1;
  }
  
  public XmlTag getChildByNameAndAttribute(String tagName, String attrKey, String attrValue) {
    if (getChildren() == null || tagName == null || "".equals(tagName) ||
        attrKey == null || attrValue == null)
    {
      return null;
    }
    for(int i = 0; i < getChildren().size(); i++) {
      XmlTag listEntry = getChildren().get(i);
      if (new XmlTag(tagName).equals(listEntry)) {
        String attributeValue = listEntry.getAttribute(attrKey);
        if (attrValue.equals(attributeValue)) {
          return listEntry;
        }
      }
    }
    
    /** if nothing found **/
    return null;
  }
  
  public String getChildByNameAndAttributeValue(String tagName, String attrKey, String attrValue) {
    XmlTag child = getChildByNameAndAttribute(tagName, attrKey, attrValue);
    if (child == null) {
      return null;
    }
    return child.getValue();
  }
  
  
  /** GETTERS AND SETTERS **/
  
  /**
   * resets last index
   */
  public void resetLastIndex() {
    mLastIndex = -1;
  }
  
  public int getLastIndex() {
    return mLastIndex;
  }
  
  /**
   * @return children of this tag
   */
  public List<XmlTag> getChildren() {
    return mChildren;
  }
  public void setChildren(List<XmlTag> children) {
    mChildren = children;
  }
  public Map<String, String> getAttributes() {
    return mAttributes;
  }
  
  public String getAttribute(String key) {
    if (mAttributes == null || key == null) {
      return null;
    }
    
    return mAttributes.get(key);
  }
  public void setAttributes(Map<String, String> attributes) {
    mAttributes = attributes;
  }
  public String getName() {
    return mName;
  }
  public String getValue() {
    return mValue;
  }
  public void setValue(String value) {
    mValue = value;
  }
  public String getChildrenRaw() {
    return mChildrenRaw;
  }
  public void setChildrenRaw(String childrenRaw) {
    mChildrenRaw = childrenRaw;
  }
  
  /** overrides **/

  /**
   * 
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((mName == null) ? 0 : mName.hashCode());
    return result;
  }

  /**
   * Compares only by name
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    XmlTag other = (XmlTag) obj;
    if (mName == null) {
      if (other.mName != null)
        return false;
    } else if (!mName.equals(other.mName))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "XmlTag [mName=" + mName + ", hasChildren=" + (mChildren != null)
        + ", mAttributes=" + mAttributes + "]";
  }
  
  public String toXml() {
    StringBuffer buffer = new StringBuffer();
    
    StringBuffer attributeBuffer = new StringBuffer();
    if (mAttributes != null) {
      Iterator<Entry<String, String>> it = mAttributes.entrySet().iterator();
      while (it.hasNext()) {
        Entry<String, String> entry = it.next();
        attributeBuffer.append(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
      }
    }
    
    buffer.append("<" + mName);
    buffer.append(attributeBuffer);
    if (mChildren == null && mValue == null) {
      buffer.append("/>");
    }
    else  {
      buffer.append(">");
      if (mValue != null) {
        buffer.append(mValue);
      }
      
      if (mChildren != null) {
        for (XmlTag tag : mChildren) {
          buffer.append(tag.toXml());
        }
      }
      buffer.append("</" + mName + ">");
    }
    
    return buffer.toString();
  }
  
}

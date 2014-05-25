package rel.explorer.e;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LinkType implements Cloneable{
	
	private String localName;
	
	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}
	
	private String displayName;
	
	private String lndateTime;
	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getLndateTime() {
		return lndateTime;
	}

	public void setLndateTime(String lndateTime) {
		this.lndateTime = lndateTime;
	}

	public String getLNDateTime() {
		return lndateTime;
	}

	public void setLNDateTime(String lndateTime) {
		this.lndateTime = lndateTime;
	}

	public boolean isShowArrows() {
		return showArrows;
	}

	public void setShowArrows(boolean showArrows) {
		this.showArrows = showArrows;
	}
	
	public void setShowArrows(String showArrows)
	{
		this.setShowArrows(Boolean.parseBoolean(showArrows));
	}

	public Map<String, CatProperty> getCatProperties() {
		return catProperties;
	}

	public void setCatProperties(Map<String, CatProperty> catProperties) {
		this.catProperties = catProperties;
	}
	
	public CatProperty getCatProperty(Object localName)
	{
		return catProperties.get(localName);
	}
	
	public void addCatProperty(CatProperty catProperty)
	{
		catProperties.put(catProperty.getLocalName(), catProperty);
	}
	
	@SuppressWarnings("unchecked") 
	public CatProperty[] getLabelCatProperty()
	{		
		List l = new ArrayList();
		CatProperty [] catProperties = (CatProperty[]) this.catProperties.values().toArray( new CatProperty[0]);
		for(int i=0; i < catProperties.length; i++){
			if( catProperties[i].isLabel())
				l.add(catProperties[i]);
		}
		return (CatProperty[]) l.toArray(new CatProperty[0]);
	}
	
	@SuppressWarnings("unchecked") 
	public CatProperty[] getTipCatProperty()
	{
		List l = new ArrayList();
		CatProperty [] catProperties = (CatProperty[]) this.catProperties.values().toArray(new CatProperty[0]);
		for(int i=0; i < catProperties.length; i++){
			if( catProperties[i].isToolTip())
				l.add(catProperties[i]);
		}
		return (CatProperty[]) l.toArray(new CatProperty[0]);
	}
	
	private boolean showArrows=false;
	
	private Map<String, CatProperty> catProperties = new LinkedHashMap<String, CatProperty>();	
	
	@Override
	public Object clone() {  
		LinkType o = null;  
        try {  
           o = (LinkType) super.clone();  
           o.catProperties = new LinkedHashMap<String, CatProperty>();
           for(String obj: catProperties.keySet()){
        	   CatProperty catProperty = catProperties.get(obj);
        	   o.catProperties.put(obj, (CatProperty) catProperty.clone());        	   
           }            
        } catch (CloneNotSupportedException e) {  
            e.printStackTrace();  
        }  
        return o;  
    }  
	
	public boolean equals(Object o)
	{
		if( !(o instanceof LinkType)) return false;
		LinkType lt = (LinkType) o;
		
		if( lt == this || (lt!=null && lt.getLocalName().equals(localName))) return true;
		
		return false;
	}
	
	public int hashCode()
	{
		return localName == null? 0 : localName.hashCode();
	}
	
	@Override
	public String toString()
	{
		return localName;
	}
}

package rel.explorer.e;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EntityType implements Cloneable{

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public Map<String, CatProperty> getCatProperties() {
		return catProperties;
	}

	public void setCatProperties(Map<String, CatProperty> catProperties) {
		this.catProperties = catProperties;
	}

	public ImageURL getImageURL() {
		return imageURL;
	}

	public void setImageURL(ImageURL imageURL) {
		this.imageURL = imageURL;
	}

	//�������
	private String localName;
	
	private String displayName;

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

	//��������
	private Map<String, CatProperty> catProperties = new LinkedHashMap<String, CatProperty>();

	//����ͼƬ
	private ImageURL imageURL = null;
	
	//ʱ������
	private String lndateTime;
	
	public String getLNDateTime() {
		return lndateTime;
	}

	public void setLNDateTime(String lndateTime) {
		this.lndateTime = lndateTime;
	}

	//չʾ
	private String representation = "icon";

	public String getRepresentation() {
		return 	representation;
	}

	public void setRepresentation(String representation) {
		//if("icons".equals(representation)||"theme".equals(representation))
		this.representation = representation;
	}

	/**
	 * ��ȡʵ��������isLabel=true��catProperty,û�з���null
	 * @return catProperty
	 */
	public CatProperty[] getLabelCatProperty() {
		List<CatProperty> l = new ArrayList<CatProperty>();
		CatProperty[] catProperties = (CatProperty[]) this.catProperties
				.values().toArray(new CatProperty[0]);
		for (int i = 0; i < catProperties.length; i++) {
			if (catProperties[i].isLabel())
				l.add( catProperties[i]);
		}
		return  (CatProperty[]) l.toArray(new CatProperty[0]);
	}

	/**
	 * ��ȡʵ��������isToolTip=true��catProperty����,û�з���null
	 * @return isToolTips=true��catProperty����
	 */
	@SuppressWarnings("unchecked") 
	public CatProperty[] getTipCatProperty() {
		List l = new ArrayList();
		CatProperty[] catProperties = (CatProperty[]) this.catProperties
				.values().toArray(new CatProperty[0]);
		for (int i = 0; i < catProperties.length; i++) {
			if (catProperties[i].isToolTip()){
				l.add(catProperties[i]);
			}
		}
		return (CatProperty[]) l.toArray(new CatProperty[0]);
	}	

	public CatProperty getCatProperty(Object localName) {
		return catProperties.get(localName);
	}
	
	public void addCatProperty(CatProperty catProperty)
	{
		catProperties.put(catProperty.getLocalName(), catProperty);
	}
	
	
	@Override
	public Object clone() {  
		EntityType o = null;  
        try {  
           o = (EntityType) super.clone();
           if( imageURL != null ){
        	   o.imageURL = (ImageURL) imageURL.clone();
           }        
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
		if( !(o instanceof EntityType)) return false;
		EntityType et = (EntityType) o;
		
		if( et == this || (et!=null && et.getLocalName().equals(localName))) return true;
		
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

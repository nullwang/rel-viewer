package com.b.e;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Administrator
 * 
 */
public class Entity implements Cloneable{

	private String catType;

	private String id;

	private int xpos = 0;

	private int ypos = 0;	

	private boolean isFixed = false;

	private boolean isHidden = false;

	private String representation = null;//Representation.ICON;

	private Formatting formatting;
	
	private Map<Object, Property> properties = new LinkedHashMap<Object, Property>();	
	
	public interface Representation{
		String ICON="icon";
		String THEME="theme";
	}

	public String getRepresentation() {
		return representation;
	}
	
	@Override
	public Object clone() {  
		Entity o = null;  
        try {  
           o = (Entity) super.clone();
           if( formatting != null ){
        	   o.formatting = (Formatting) formatting.clone();
           }
           o.properties = new LinkedHashMap<Object, Property>();
           for(Object obj: properties.keySet()){
        	   Property property = properties.get(obj);
        	   o.properties.put(obj, (Property) property.clone());        	   
           }            
        } catch (CloneNotSupportedException e) {  
            e.printStackTrace();  
        }  
        return o;  
    }  
	
	public boolean checkRepresentation(String representation){
		if(Representation.ICON.equals(representation)
				||Representation.THEME.equals(representation) )
			return true;
		return false;
		
	}
	
	public void setRepresentation(String representation) {
		//if(checkRepresentation(representation))
		this.representation = representation;
	}

	public String getCatType() {
		return catType;
	}

	public void setCatType(String catType) {
		this.catType = catType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}	
	
	public int getXpos() {
		return xpos;
	}

	public void setXpos(int xpos) {
		this.xpos = xpos;
	}

	public int getYpos() {
		return ypos;
	}

	public void setYpos(int ypos) {
		this.ypos = ypos;
	}

	public void setXpos(String xpos)
	{
		try {
			this.setXpos(Integer.parseInt(xpos));
		}catch(NumberFormatException ne){}
	}

	
	public void setYpos(String ypos)
	{
		try {
			this.setYpos(Integer.parseInt(ypos));
		}catch(NumberFormatException ne){}
	}

	public Formatting getFormatting() {
		return formatting;
	}

	public void setFormatting(Formatting formatting) {
		this.formatting = formatting;
	}

	public boolean isFixed() {
		return isFixed;
	}

	public void setFixed(boolean isFixed) {
		this.isFixed = isFixed;
	}
	
	public void setFixed(String isFixed){
		this.setFixed(Boolean.parseBoolean(isFixed));
	}

	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}
	
	public void setHidden(String isHidden){
		this.setHidden(Boolean.parseBoolean(isHidden));
	}
	
	public Map<Object, Property> getProperties() {
		return properties;
	}

	public void setProperties(Map<Object, Property> properties) {
		this.properties = properties;
	}
	
	/**
	 * ����������ȡ����
	 * @param name
	 * @return
	 */
	public Property getProperty(Object name)
	{
		return properties.get(name);
	}
	
	public void addProperty(Property property)
	{
		properties.put(property.getName(),property	);
	}
	
	public boolean equals(Object o)
	{
		if( !(o instanceof Entity)) return false;
		
		Entity e = (Entity)o;
		
		if( e== this || ( e != null && e.getId().equals(id)) ) return true;
		
		return false;
	}
	
	public int hashCode()
	{
		return (id ==null)? 0: id.hashCode();
	}
	
	@Override
	public String toString()
	{
		return id;
	}	
}

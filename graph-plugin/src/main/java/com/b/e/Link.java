package com.b.e;

import java.util.LinkedHashMap;
import java.util.Map;

public class Link implements Cloneable{
	private String catType;
	
	private String id;
	
	private String direction=Direction.NONE;
	
	private String ent1Id;
	
	private String ent2Id;
	
	private String dotStyle=DotStyle.CONFIRMED;
	
	private String color;
	
	private int lineThickness;
	
	private boolean isHidden = false;
	
	private int xpos=0; 	
	
	private Map<Object, Property> properties = new LinkedHashMap<Object, Property>();	
	
	public interface Direction{
		String NONE= "none";
		String FORWARD="forward";
		String REVERSE="reverse";
		String BOTH="both";
	}
	
	public interface DotStyle{
		String CONFIRMED = "confirmed";
		String TENTATIVE="tentative";
		String UNCONFIRMED="unconfirmed";
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

	public String getDirection() {
		return direction;
	}
	
	public boolean checkDirection(String direction)
	{
		if ( Link.Direction.NONE.equals(direction)
		|| Link.Direction.FORWARD.equals(direction)		
		|| Link.Direction.REVERSE.equals(direction)
		|| Link.Direction.BOTH.equals(direction))
			return true;
		
		return false;
	}

	public void setDirection(String direction) {
		if(checkDirection(direction))
		
		this.direction = direction;
	}

	public String getEnt1Id() {
		return ent1Id;
	}

	public void setEnt1Id(String ent1Id) {
		this.ent1Id = ent1Id;
	}

	public String getEnt2Id() {
		return ent2Id;
	}

	public void setEnt2Id(String ent2Id) {
		this.ent2Id = ent2Id;
	}
	
	public void addProperty(Property property)
	{
		this.properties.put(property.getName(), property);
	}
	
	public void removeProperty(Property Property)
	{
		this.properties.remove(Property.getName());
	}

	public String getDotStyle() {
		return dotStyle;
	}

	public boolean checkDotStyle(String dotStyle){
		if(Link.DotStyle.CONFIRMED.equals(dotStyle)
				|| Link.DotStyle.UNCONFIRMED.equals(dotStyle)
				|| Link.DotStyle.TENTATIVE.equals(dotStyle))
			
			return true;
		return false;
		
	}
	public void setDotStyle(String dotStyle) {
		if("confirmed".equals(dotStyle)
				|| "tentative".equals(dotStyle)
				|| "unconfirmed".equals(dotStyle))
		this.dotStyle = dotStyle;
	}

	public int getLineThickness() {
		return lineThickness;
	}

	public void setLineThickness(int lineThickness) {
		this.lineThickness = lineThickness;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}
	
	public void setHidden(String isHidden)
	{
		this.setHidden(Boolean.parseBoolean(isHidden));
	}

	public int getXpos() {
		return xpos;
	}

	public void setXpos(int xpos) {
		this.xpos = xpos;
	}
	
	public void setXpos(String xpos)
	{
		try {
			this.setXpos(Integer.parseInt(xpos));
		}catch(NumberFormatException ne){}
	}
	
	public void setLineThickness(String lineThickness)
	{
		try {
			this.setLineThickness(Integer.parseInt(lineThickness));
		}catch(NumberFormatException ne){}
	}

	public Map<Object, Property> getProperties() {
		return properties;
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

	public void setProperties(Map<Object, Property> properties) {
		this.properties = properties;
	}
	
	public  boolean equals(Object o)
	{
		if( !(o instanceof Link)) return false;
		
		Link l = (Link) o;
		
		if( l == this || ( l!=null && l.getId().equals(id))) return true;
		
		return false;
	}
	
	public int hashCode()
	{
		return (id ==null)? 0: id.hashCode();
	}
	
	@Override
	public Object clone() {  
		Link o = null;  
        try {  
           o = (Link) super.clone();       
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
	
	@Override
	public String toString()
	{
		return id;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}

package com.b.e;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class TypeCatalogue implements Cloneable{
	
	String id; //catalog��ʶ��
	
	Map<Object,EntityType> entityTypes = new LinkedHashMap<Object,EntityType>();
	
	Map<Object,LinkType> linkTypes =  new LinkedHashMap<Object,LinkType>();
	
	Map<Object,Form> forms = new LinkedHashMap<Object,Form>();
	
	Map<Object,Service> services = new LinkedHashMap<Object,Service>();
	
	public void addEntityType(EntityType entityType)
	{
		entityTypes.put(entityType.getLocalName(), entityType);		
	}
	
	public void addEntityTypes(Collection<EntityType> collection)
	{
		for(EntityType obj: collection){
			this.addEntityType(obj);
		}
	}
	
	public void addLinkType(LinkType linkType)
	{
		linkTypes.put(linkType.getLocalName(), linkType);
	}
	
	public void addLinkTypes(Collection<LinkType> collection)
	{
		for(LinkType obj: collection){
			this.addLinkType(obj);
		}
	}
	
	public void addForm(Form form)
	{
		forms.put(form.getFguid(),form);
	}
	
	public void addForms(Collection<Form> collection)
	{
		for(Form obj:collection){
			this.addForm(obj);
		}
	}
	
	public void addService(Service service)
	{
		services.put(service.getSguid(), service);
	}
	
	public void addServices(Collection<Service> collection)
	{
		for(Service service:collection){
			this.addService(service);
		}
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}	
	
	public Map<Object, EntityType> getEntityTypes() {
		return entityTypes;
	}

	public void setEntityTypes(Map<Object, EntityType> entityTypes) {
		this.entityTypes = entityTypes;
	}

	public Map<Object, LinkType> getLinkTypes() {
		return linkTypes;
	}

	public void setLinkTypes(Map<Object, LinkType> linkTypes) {
		this.linkTypes = linkTypes;
	}

	public Map<Object, Form> getForms() {
		return forms;
	}

	public void setForms(Map<Object, Form> forms) {
		this.forms = forms;
	}

	public Map<Object, Service> getServices() {
		return services;
	}

	public void setServices(Map<Object, Service> services) {
		this.services = services;
	}
	
	@Override
	public  Object clone()
	{
		TypeCatalogue o = null;
		
			try {
				o = (TypeCatalogue) super.clone();
				o.entityTypes = new LinkedHashMap<Object,EntityType>();				
				o.linkTypes =  new LinkedHashMap<Object,LinkType>();
				o.forms = new LinkedHashMap<Object,Form>();
				o.services = new LinkedHashMap<Object,Service>(); 
				
				for( Object obj : entityTypes.keySet()){
					o.entityTypes.put(obj, (EntityType) entityTypes.get(obj).clone());
				}
				for( Object obj:linkTypes.keySet()){
					o.linkTypes.put(obj, (LinkType) linkTypes.get(obj).clone());
				}
				for(Object obj:forms.keySet()){
					o.forms.put(obj, (Form) forms.get(obj).clone());
				}
				for(Object obj:services.keySet()){
					o.services.put(obj, (Service) services.get(obj).clone());
				}
				
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			
		return o;
		
	}

	public boolean equals(Object o)
	{
		if( ! (o instanceof TypeCatalogue))
			return false;
		TypeCatalogue tc = (TypeCatalogue) o;
		if( this == tc || ( tc != null && tc.getId().equals(id) ) )
			return true;
		

        return false;
	}
	
	public int hashCode()
	{
		return (id == null) ? 0 :id.hashCode();
	}
}

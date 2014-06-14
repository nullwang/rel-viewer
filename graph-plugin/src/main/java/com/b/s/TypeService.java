package com.b.s;

import java.util.Collection;

import com.b.e.EntityType;
import com.b.e.Form;
import com.b.e.LinkType;
import com.b.e.Service;
import com.b.e.TypeCatalogue;

public interface TypeService {
	
	public void addCatalogue(TypeCatalogue typeCatlogue);
	
	public void removeCatalogue(Object id);
	
	public void setDefaultCatalogue(Object id);
	
	public TypeCatalogue getCatalogue(Object id);
	
	public Collection<TypeCatalogue> getCatalogues();
	
	/**
	 */
	public TypeCatalogue getDefaultCatalogue();	
	
	public Collection<LinkType> getLinkTypes();
	
	public Collection<EntityType> getEntityTypes();
	
	public LinkType getLinkType(Object localName);
	
	public EntityType getEntityType(Object localName); 
	
	public Form getForm(Object id);	
	
	public Collection<Form> getForms();
	
	public Service getService(Object id);
	
	public Collection<Service> getServices();
	
	public void clear();
	
	public void removeLinkTypes(Collection<LinkType> linkType);
	
	public void removeEntityTypes(Collection<EntityType> entityType);
	
	public void removeLinkType(LinkType linkType);
	
	public void removeLinkType(Object localName);
	
	public void removeEntityType(EntityType entityType);
	
	public void removeEntityType(Object localName);
	
	public void mergeTypeService( TypeService	typeService	);
	
	public boolean isLinkType(Object id);
	
	public boolean isEntityType(Object id);
}
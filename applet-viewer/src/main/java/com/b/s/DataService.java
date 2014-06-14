package com.b.s;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b.e.CatProperty;
import com.b.e.Entity;
import com.b.e.EntityType;
import com.b.e.Form;
import com.b.e.Formatting;
import com.b.e.ImageURL;
import com.b.e.Link;
import com.b.e.LinkType;
import com.b.e.Property;
import com.b.e.Service;
import com.b.e.TypeCatalogue;
import com.b.e.Entity.Representation;

public class DataService {

	private TypeService typeService;
	private EntityService entityService;
	private DataService mergeData;

	private Map<Object, Entity> delEntities =  Collections.synchronizedMap(new HashMap<Object, Entity>());
	private Map<Object, Set<Object>> delEntityLinks = Collections.synchronizedMap( new HashMap<Object, Set<Object>>());
	private Map<Object, Link> delLinks = Collections.synchronizedMap( new HashMap<Object, Link>());

	private Set<Object> hiddenEntities = Collections.synchronizedSet(new HashSet<Object>());
	private Map<Object, Set<Object>> hiddenEntityLinks = Collections.synchronizedMap( new HashMap<Object, Set<Object>>());
	private Set<Object> hiddenLinks = Collections.synchronizedSet( new HashSet<Object>());
	private Map<Object,String> linkShape =  Collections.synchronizedMap( new HashMap<Object, String>());
	
	private Map<Object,Boolean> lockEntities = Collections.synchronizedMap(new HashMap<Object,Boolean>());
	
	class TypeServiceImpl implements TypeService {
		Map<Object, TypeCatalogue> cats = Collections.synchronizedMap(new HashMap<Object, TypeCatalogue>());
		Object defaultId;

		public void addCatalogue(TypeCatalogue typeCatlogue) {
			Object id = typeCatlogue.getId();
			TypeCatalogue exsitTypeCatlogue = this.getCatalogue(id);
			if (exsitTypeCatlogue != null) {
				exsitTypeCatlogue.getEntityTypes().putAll(
						typeCatlogue.getEntityTypes());
				exsitTypeCatlogue.getLinkTypes().putAll(
						typeCatlogue.getLinkTypes());
				exsitTypeCatlogue.getForms().putAll(typeCatlogue.getForms());
				exsitTypeCatlogue.getServices().putAll(
						typeCatlogue.getServices());
			} else
				cats.put(id, typeCatlogue);
		}

		public TypeCatalogue getCatalogue(Object id) {
			return cats.get(id);
		}

		public TypeCatalogue getDefaultCatalogue() {
			return this.getCatalogue(this.defaultId);
		}

		/**
		 * 
		 * @return
        */
		public EntityType getEntityType(Object localName) {
			EntityType et = null;
			TypeCatalogue typeCatalogue = this.getDefaultCatalogue();
			if (typeCatalogue != null) {
				et = typeCatalogue.getEntityTypes().get(localName);
			}
			if (et == null) {
				for (TypeCatalogue cat : cats.values()) {
					et = cat.getEntityTypes().get(localName);
					if (et != null)
						return et;
				}
			}

			return et;
		}

		public Collection<EntityType> getEntityTypes() {
			Collection<EntityType> c = new HashSet<EntityType>();
			for (TypeCatalogue cat : cats.values()) {
				c.addAll(cat.getEntityTypes().values());
			}
			return c;
		}

		public Form getForm(Object id) {
			Form f = null;
			TypeCatalogue typeCatalogue = this.getDefaultCatalogue();
			if (typeCatalogue != null) {
				f = typeCatalogue.getForms().get(id);
			}
			if (f == null) {
				for (TypeCatalogue cat : cats.values()) {
					f = cat.getForms().get(id);
					if (f != null)
						return f;
				}
			}

			return f;
		}

		public LinkType getLinkType(Object localName) {
			LinkType lt = null;

			TypeCatalogue typeCatalogue = this.getDefaultCatalogue();
			if (typeCatalogue != null) {
				lt = typeCatalogue.getLinkTypes().get(localName);
			}
			if (lt == null) {
				for (TypeCatalogue cat : cats.values()) {
					lt = cat.getLinkTypes().get(localName);
					if (lt != null)
						return lt;
				}
			}

			return lt;
		}

		public Collection<LinkType> getLinkTypes() {
			Collection<LinkType> c = new HashSet<LinkType>();
			for (TypeCatalogue cat : cats.values()) {
				c.addAll(cat.getLinkTypes().values());
			}
			return c;
		}

		public Service getService(Object id) {
			Service s = null;
			TypeCatalogue typeCatalogue = this.getDefaultCatalogue();
			if (typeCatalogue != null) {
				s = typeCatalogue.getServices().get(id);
			}
			if (s == null) {
				for (TypeCatalogue cat : cats.values()) {
					s = cat.getServices().get(id);
					if (s != null)
						return s;
				}
			}

			return s;
		}

		public void removeCatalogue(Object id) {
			this.cats.remove(id);

		}

		public void setDefaultCatalogue(Object id) {
			this.defaultId = id;

		}

		public Collection<Form> getForms() {
			Collection<Form> c = new HashSet<Form>();
			for (TypeCatalogue cat : cats.values()) {
				c.addAll(cat.getForms().values());
			}
			return c;
		}

		public Collection<Service> getServices() {
			Collection<Service> c = new HashSet<Service>();
			for (TypeCatalogue cat : cats.values()) {
				c.addAll(cat.getServices().values());
			}
			return c;
		}

		public void clear() {
			this.cats.clear();
		}

		public void removeEntityType(EntityType entityType) {
			this.removeEntityType(entityType.getLocalName());
		}

		public void removeEntityType(Object localName) {
			for (TypeCatalogue cat : cats.values()) {
				cat.getEntityTypes().remove(localName);
			}
		}

		public void removeEntityTypes(Collection<EntityType> c) {
			for (EntityType entityType : c) {
				this.removeEntityType(entityType);
			}
		}

		public void removeLinkType(LinkType linkType) {
			this.removeLinkType(linkType.getLocalName());
		}

		public void removeLinkType(Object localName) {
			for (TypeCatalogue cat : cats.values()) {
				cat.getLinkTypes().remove(localName);
			}
		}

		public void removeLinkTypes(Collection<LinkType> c) {
			for (LinkType linkType : c) {
				this.removeLinkType(linkType);
			}
		}

		public Collection<TypeCatalogue> getCatalogues() {
			return cats.values();
		}

		public void mergeTypeService(TypeService typeService) {
			for (TypeCatalogue typeCatlogue : typeService.getCatalogues()) {
				this.addCatalogue(typeCatlogue);
			}
		}

		public boolean isEntityType(Object id) {
			for (TypeCatalogue cat : cats.values()) {
				if (cat.getEntityTypes().containsKey(id))
					return true;
			}
			return false;
		}

		public boolean isLinkType(Object id) {
			for (TypeCatalogue cat : cats.values()) {
				if (cat.getLinkTypes().containsKey(id))
					return true;
			}
			return false;
		}
	}

	class EntityServiceImpl implements EntityService {
		Map<Object, Entity> entities = Collections.synchronizedMap( new HashMap<Object, Entity>());
		Map<Object, Link> links = Collections.synchronizedMap( new HashMap<Object, Link>());

		public void addEntity(Entity et) {
			entities.put(et.getId(), et);
		}

		public void addEntities(Collection<Entity> c) {
			for (Entity entity : c) {
				this.addEntity(entity);
			}
		}

		public void addLink(Link link) {
			links.put(link.getId(), link);
		}

		public void addLinks(Collection<Link> c) {
			for (Link link : c) {
				this.addLink(link);
			}
		}

		public Entity getEntity(Object id) {
			return entities.get(id);
		}

		public Collection<Entity> getEntities() {
			return entities.values();
		}

		public Link getLink(Object id) {
			return links.get(id);
		}

		public Collection<Link> getLinks() {
			return links.values();
		}

		public void clear() {
			this.entities.clear();
			this.links.clear();
		}

		public void removeEntity(Entity entity) {
			this.removeEntity(entity.getId());
		}

		public void removeEntity(Object id) {
			this.entities.remove(id);
		}

		public void removeEntities(Collection<Entity> c) {
			for (Entity entity : c) {
				this.removeEntity(entity);
			}
		}

		public void removeLink(Object id) {
			this.links.remove(id);
		}

		public void removeLink(Link link) {
			this.removeLink(link.getId());
		}

		public void removeLinks(Collection<Link> c) {
			for (Link link : c) {
				this.removeLink(link);
			}
		}

		// check entity or link exist
		public void mergeEntityService(EntityService es) {
			Iterator it = es.getEntities().iterator();
			while (it.hasNext()) {
				Entity e = (Entity) it.next();
				if (this.getEntity(e.getId()) == null) {
					this.addEntity(e);
				} else {
					it.remove();
				}

			}

			it = es.getLinks().iterator();
			while (it.hasNext()) {
				Link l = (Link) it.next();

				if (this.getLink(l.getId()) == null) {
					this.addLink(l);
				} else {
					it.remove();
				}
			}
			// this.addEntities(es.getEntities());
			// this.addLinks(es.getLinks());
		}

		public boolean isEntity(Object id) {
			return entities.containsKey(id);
		}

		public boolean isLink(Object id) {
			return links.containsKey(id);
		}
	}

	public DataService() {
		this(null, null);
	}

	public DataService(TypeService typeService, EntityService entityService) {
		typeService = (typeService == null) ? new TypeServiceImpl()
				: typeService;
		entityService = (entityService == null) ? new EntityServiceImpl()
				: entityService;
		this.setTypeService(typeService);
		this.setEntityService(entityService);
	}

	public void clear() {
		typeService.clear();
		entityService.clear();
	}

	 synchronized public void merge(DataService ds) {
		
				this.mergeData = ds;
				if (mergeData == null)
					mergeData = new DataService();
				this.getTypeService().mergeTypeService(
						mergeData.getTypeService());
				this.getEntityService().mergeEntityService(
						mergeData.getEntityService());
				
	}

	 public void delEntity(Object id) {
		Entity entity = this.getEntityService().getEntity(id);
		delEntities.put(id, entity);
		this.getEntityService().removeEntity(id);

		Collection<Link> c = this.getLinks(id);
		Set<Object> s = new HashSet<Object>();
		for (Link link : c) {
			delLink(link.getId());
			s.add(link.getId());
		}

		delEntityLinks.put(id, s);
	}

	 public void delLink(Object id) {
		Link link = this.getEntityService().getLink(id);
		delLinks.put(id, link);
		this.getEntityService().removeLink(id);
	}

	@SuppressWarnings("unchecked")
	public void unDelEntity(Object id) {
		Entity entity = delEntities.get(id);
		if (entity == null)
			return;

		delEntities.remove(id);
		this.getEntityService().addEntity(entity);

		Set set = delEntityLinks.get(id);
		for (Object obj : set) {
			unDelLink(obj);
		}

		delEntityLinks.remove(id);
	}

	public void unDelLink(Object id) {
		Link link = delLinks.get(id);
		if (link == null)
			return;

		delLinks.remove(id);
		this.getEntityService().addLink(link);
	}

	@SuppressWarnings("unchecked")
	public void setEntityHidden(Object id, boolean isHidden) {
		if (isHidden) {
			hiddenEntities.add(id);

			Collection<Link> c = this.getLinks(id);
			Set<Object> s = new HashSet<Object>();
			for (Link link : c) {
				setLinkHidden(link.getId(), isHidden);
				s.add(link.getId());
			}
			hiddenEntityLinks.put(id, s);
		} else {
			hiddenEntities.remove(id);

			Set set = hiddenEntityLinks.get(id);
			for (Object obj : set) {
				setLinkHidden(obj, isHidden);
			}

			hiddenEntityLinks.remove(id);
		}
	}

	public void setLinkHidden(Object id, boolean isHidden) {
		if (isHidden) {
			hiddenLinks.add(id);
		} else {
			hiddenLinks.remove(id);
		}
	}

	public Collection<Link> getAddedLinks() {
		return mergeData.getEntityService().getLinks();
	}

	public Collection<Link> getAllLinks() {
		return this.getEntityService().getLinks();
	}

	public Collection<Entity> getAddedEnds() {
		return mergeData.getEntityService().getEntities();
	}

	public Collection<Entity> getAllEnds() {
		return this.getEntityService().getEntities();
	}

	public TypeService getTypeService() {
		return typeService;
	}

	public void setTypeService(TypeService typeService) {
		this.typeService = typeService;
	}

	public EntityService getEntityService() {
		return entityService;
	}

	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}

	public EntityType getEntityType(Object id) {
		return this.getEntityType(this.getEntityService().getEntity(id));
	}

	public EntityType getEntityType(Entity entity) {
		if (entity == null)
			return null;
		Object typeName = entity.getCatType();
		EntityType et = typeService.getEntityType(typeName);
		return et;
	}
	
	/**
	 * @param id
	 * @param type
	 * @return
	 */
	public String getEntityTypeName(Object id)
	{
		Entity ent = this.getEntityService().getEntity(id);
		if( ent != null) 
			return ent.getCatType(); 
				
		return null;
	}

	public LinkType getLinkType(Object id) {
		return this.getLinkType(this.getEntityService().getLink(id));
	}

	public LinkType getLinkType(Link link) {
		if (link == null)
			return null;
		Object typeName = link.getCatType();

		LinkType lt = typeService.getLinkType(typeName);

		return lt;
	}

	/**
	 * @param id
	 * @return
	 */
	public String[] getEntityLabel(Object id) {
		return this.getEntityLabel(entityService.getEntity(id));
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	public String[] getEntityLabel(Entity entity) {
		List<String> l = new ArrayList<String>();

		EntityType et = this.getEntityType(entity);
		if (et == null)
			return (String[]) l.toArray(new String[0]);

		CatProperty[] cts = et.getLabelCatProperty();

		for (int i = 0; i < cts.length; i++) {
			String catPropName = cts[i].getLocalName();
			Property property = entity.getProperty(catPropName);
			if (property != null) {
				l.add(property.getText());
			}
		}
		return (String[]) l.toArray(new String[0]);
	}
	
	public CatProperty[] getEntityLabelCatProperty(Object id)
	{
		return this.getEntityLabelCatProperty(entityService.getEntity(id));
	}
	
	public CatProperty[] getEntityLabelCatProperty(Entity entity)
	{
		List<CatProperty> l = new ArrayList<CatProperty>();
		
		EntityType et = this.getEntityType(entity);
		if (et != null)
		{
			return et.getLabelCatProperty();
		}
		
		return l.toArray(new CatProperty[0]);
	}
	
	public CatProperty[] getEntityToolTipCatProperty(Object id)
	{
		return getEntityToolTipCatProperty(entityService.getEntity(id));
	}
	
	public CatProperty[] getEntityToolTipCatProperty(Entity entity)
	{
		List<CatProperty> l = new ArrayList<CatProperty>();
		
		EntityType et = this.getEntityType(entity);
		if (et != null)
		{
			return et.getTipCatProperty();
		}
		
		return l.toArray(new CatProperty[0]);
	}
	
	public CatProperty[] getLinkLabelCatProperty(Object id)
	{
		return this.getLinkLabelCatProperty(entityService.getLink(id));
	}
	
	public CatProperty[] getLinkLabelCatProperty(Link link)
	{
		List<CatProperty> l = new ArrayList<CatProperty>();
		
		LinkType lt = this.getLinkType(link);
		if (lt != null)
		{
			return lt.getLabelCatProperty();
		}
		
		return l.toArray(new CatProperty[0]);
	}	
	
	public CatProperty[] getLinkToolTipCatProperty(Object id)
	{
		return getLinkToolTipCatProperty(entityService.getLink(id));
	}
	
	public CatProperty[] getLinkToolTipCatProperty(Link link)
	{
		List<CatProperty> l = new ArrayList<CatProperty>();
		
		LinkType lt = this.getLinkType(link);
		if (lt != null)
		{
			return lt.getTipCatProperty();
		}
		
		return l.toArray(new CatProperty[0]);
	}

	/**
	 * ��ȡlink��ǩ
	 * 
	 * @param id
	 * @return
	 */
	public String[] getLinkLabel(Object id) {
		return this.getLinkLabel(entityService.getLink(id));
	}

	public String[] getLinkLabel(Link link) {
		List<String> l = new ArrayList<String>();

		LinkType lt = this.getLinkType(link);
		if (lt == null)
			return (String[]) l.toArray(new String[0]);

		CatProperty[] cts = lt.getLabelCatProperty();
		for (int i = 0; i < cts.length; i++) {
			String catPropName = cts[i].getLocalName();
			Property property = link.getProperty(catPropName);
			if (property != null) {
				l.add(property.getText());
			}
		}
		return (String[]) l.toArray(new String[0]);
	}

	public Collection<CatProperty> getEntityCatProperty(Object id) {
		return this.getEntityCatProperty(this.entityService.getEntity(id));
	}

	public Collection<CatProperty> getEntityCatProperty(Entity entity) {
		List<CatProperty> l = new ArrayList<CatProperty>();
		EntityType et = this.getEntityType(entity);

		if (et == null)
			return l;

		return et.getCatProperties().values();
	}

	public CatProperty getEntityCatProperty(Entity entity, Object name) {
		EntityType et = getEntityType(entity);
		return et == null ? null : et.getCatProperty(name);
	}

	public CatProperty getEntityCatProperty(Object id, Object name) {
		return this
				.getEntityCatProperty(this.entityService.getEntity(id), name);
	}

	public CatProperty getLinkCatProperty(Link link, Object name) {
		LinkType lt = getLinkType(link);
		return lt == null ? null : lt.getCatProperty(name);
	}

	public CatProperty getLinkCatProperty(Object id, Object name) {
		return this.getLinkCatProperty(this.entityService.getLink(id), name);
	}

	public Collection<CatProperty> getLinkCatProperty(Object id) {
		return this.getLinkCatProperty(this.getEntityService().getLink(id));
	}

	public Collection<CatProperty> getLinkCatProperty(Link link) {
		List<CatProperty> l = new ArrayList<CatProperty>();

		LinkType lt = this.getLinkType(link);
		if (lt == null)
			return l;

		return lt.getCatProperties().values();
	}

	public String[] getEntityToolTips(Object id) {
		return this.getEntityToolTips(this.entityService.getEntity(id));
	}

	public String[] getEntityToolTips(Entity entity) {
		List<String> l = new ArrayList<String>();

		EntityType et = this.getEntityType(entity);
		if (et == null)
			return (String[]) l.toArray(new String[0]);

		CatProperty[] cts = et.getTipCatProperty();
		for (int i = 0; i < cts.length; i++) {
			String catPropName = cts[i].getLocalName();
			Property property = entity.getProperty(catPropName);
			if (property != null) {
				l.add(property.getText());
			}
		}
		return (String[]) l.toArray(new String[0]);
	}

	public String[] getLinkToolTips(Object id) {
		return this.getLinkToolTips(this.entityService.getLink(id));
	}

	public String[] getLinkToolTips(Link link) {
		List<String> l = new ArrayList<String>();
		LinkType lt = this.getLinkType(link);
		if (lt == null)
			return (String[]) l.toArray(new String[0]);

		CatProperty[] cts = lt.getTipCatProperty();
		for (int i = 0; i < cts.length; i++) {
			String catPropName = cts[i].getLocalName();
			Property property = link.getProperty(catPropName);
			if (property != null) {
				l.add(property.getText());
			}
		}
		return (String[]) l.toArray(new String[0]);

	}

	/**
	 * 
	 * @return
	 */
	public List<ImageURL> getEntityImages(Entity entity) {
		List<ImageURL> images = new ArrayList<ImageURL>();

		Formatting formatting = entity.getFormatting();
		if (formatting != null) {
			images.addAll(formatting.getImgs());
		}
		EntityType et = getEntityType(entity);
		if (et != null) {
			images.add(et.getImageURL());
		}

		return images;
	}

	public List<ImageURL> getEntityImages(Object id) {
		return this.getEntityImages(this.entityService.getEntity(id));
	}

	public ImageURL getEntityTypeImage(Entity entity) {
		EntityType et = getEntityType(entity);
		return et == null ? null : et.getImageURL();
	}

	public ImageURL getEntityTypeImage(Object id) {
		return this.getEntityTypeImage(this.entityService.getEntity(id));
	}
	
	public String getEntityPropertyValue(Object entity, Object name)
	{
		Property property = getEntityProperty(entity,name);
		return property  == null ? null : property.getText();
	}
	
	public String getEntityPropertyValue(Entity entity, Object name)
	{
		Property property = getEntityProperty(entity,name);
		return property  == null ? null : property.getText();
	}

	public Property getEntityProperty(Entity entity, Object name) {
		return entity == null ? null : entity.getProperty(name);
	}

	public Property getEntityProperty(Object id, Object name) {
		return this.getEntityProperty(this.entityService.getEntity(id), name);
	}
	
	public String getLinkPropertyValue(Object link, Object name)
	{
		Property property = getLinkProperty(link,name);
		return property  == null ? null : property.getText();
	}
	
	public String getLinkPropertyValue(Link link, Object name)
	{
		Property property = getLinkProperty(link,name);
		return property  == null ? null : property.getText();
	}

	public Property getLinkProperty(Link link, Object name) {
		return link == null ? null : link.getProperty(name);
	}

	public Property getLinkProperty(Object id, Object name) {
		return this.getLinkProperty(this.entityService.getLink(id), name);
	}

	public void setEntityPropertyValue(Entity entity, Object name,
			String value, boolean isToolTip) {
		CatProperty catProperty = this.getEntityCatProperty(entity, name);
		if (catProperty == null)
			return;
		catProperty.setToolTip(isToolTip);

		Property property = this.getEntityProperty(entity, catProperty
				.getLocalName());

		if (property != null)
			property.setText(value);

	}

	public void setEntityPropertyValue(Object id, Object name, String value,
			boolean isToolTip) {
		this.setEntityPropertyValue(this.entityService.getEntity(id), name,
				value, isToolTip);
	}

	public void setLinkPropertyValue(Link link, Object name, String value,
			boolean isToolTip) {
		CatProperty catProperty = this.getLinkCatProperty(link, name);
		if (catProperty == null)
			return;
		catProperty.setToolTip(isToolTip);

		Property property = this.getLinkProperty(link, catProperty
				.getLocalName());

		if (property != null)
			property.setText(value);

	}

	public void setLinkPropertyValue(Object id, Object name, String value,
			boolean isTooltip) {
		this.setLinkPropertyValue(this.entityService.getLink(id), name, value,
				isTooltip);
	}

	public boolean isLink(Object id) {
		if (entityService.isLink(id))
			return true;
		else
			return this.delLinks.containsKey(id);
	}

	public boolean isEntity(Object id) {
		if (this.entityService.isEntity(id))
			return true;
		else
			return this.delEntities.containsKey(id);
	}

	public boolean isLinkType(Object id) {
		return this.typeService.isLinkType(id);
	}

	public boolean isEntityType(Object id) {
		return this.typeService.isEntityType(id);
	}

	public boolean isEntityHidden(Object id) {
		return this.hiddenEntities.contains(id);
	}

	public boolean isEntityHidden(Entity entity) {
		return isEntityHidden(entity.getId());
	}

	public boolean isLinkHidden(Object id) {
		return this.hiddenLinks.contains(id);
	}

	public boolean isLinkHidden(Link link) {
		return isLinkHidden(link.getId());
	}

	public boolean isEntityDeleted(Object id) {
		return this.delEntities.containsKey(id);
	}

	public boolean isEntityDeleted(Entity entity) {
		return isEntityDeleted(entity.getId());
	}

	public boolean isLinkDeleted(Object id) {
		return this.delLinks.containsKey(id);
	}

	public boolean isLinkDeleted(Link link) {
		return this.isLinkDeleted(link.getId());
	}

	public Collection<Object> getHiddenEntities() {
		return this.hiddenEntities;
	}

	public Collection<Object> getHiddenLinks() {
		return this.hiddenLinks;
	}

	public Collection<Entity> getDeletedEntities() {
		return this.delEntities.values();
	}

	public Collection<Link> getDeletedLinks() {
		return this.delLinks.values();
	}

	/**
	 *
	 * 
	 * @param id
	 *
	 * @return
	 */
	public Collection<Link> getLinks(Object id) {
		List<Link> c = new ArrayList<Link>();
		c.addAll(getInLinks(id));
		c.addAll(getOutLinks(id));
		return c;
	}

	public Collection<Link> getOutLinks(Object id) {
		List<Link> c = new ArrayList<Link>();

		for (Link link : getEntityService().getLinks()) {
			if (id.equals(link.getEnt2Id())) {
				c.add(link);
			}
		}

		return c;
	}

	public Collection<Link> getInLinks(Object id) {
		List<Link> c = new ArrayList<Link>();

		for (Link link : getEntityService().getLinks()) {
			if (id.equals(link.getEnt1Id())) {
				c.add(link);
			}
		}

		return c;
	}

	public Collection<Entity> getVisibleEntities() {
		List<Entity> al = new ArrayList<Entity>();

		for (Entity entity : getEntityService().getEntities()) {
			if (!isEntityHidden(entity))
				al.add(entity);
		}
		return al;
	}

	public Collection<Link> getVisibleLinks() {
		List<Link> al = new ArrayList<Link>();

		for (Link link : getEntityService().getLinks()) {
			if (!isLinkHidden(link))
				al.add(link);
		}
		return al;
	}

	public Collection<Entity> findEntities(String text, boolean blnExactMatch) {
		List<Entity> al = new ArrayList<Entity>();
		for (Entity entity : this.getEntityService().getEntities()) {
			if (entityContainsLabel(entity, text, blnExactMatch)) {
				al.add(entity);
			}
			if (entityContainsToolTip(entity, text, blnExactMatch)) {
				al.add(entity);
			}
		}
		return al;
	}

	public Collection<Link> findLinks(String text, boolean blnExactMatch) {
		List<Link> al = new ArrayList<Link>();
		for (Link link : getEntityService().getLinks()) {
			if (linkContainsToolTip(link, text, blnExactMatch))
				al.add(link);
			if (linkContainsLabel(link, text, blnExactMatch))
				al.add(link);
		}
		return al;
	}

	public boolean entityContainsToolTip(Object id, String toolTip,
			boolean blnExactMatch) {
		return entityContainsToolTip(getEntityService().getEntity(id), toolTip,
				blnExactMatch);
	}

	public boolean entityContainsToolTip(Entity entity, String toolTip,
			boolean blnExactMatch) {
		String[] tips = this.getEntityToolTips(entity);

		for (int i = 0; i < tips.length; i++) {
			if (blnExactMatch) {
				if (tips[i].equals(toolTip))
					return true;
			} else {
				if (tips[i].contains(toolTip))
					return true;
			}
		}
		return false;
	}

	public boolean linkContainsToolTip(Object id, String toolTip,
			boolean blnExactMatch) {
		return linkContainsToolTip(getEntityService().getLink(id), toolTip,
				blnExactMatch);
	}

	public boolean linkContainsToolTip(Link link, String toolTip,
			boolean blnExactMatch) {
		String[] tips = this.getLinkToolTips(link);

		for (int i = 0; i < tips.length; i++) {
			if (blnExactMatch) {
				if (tips[i].equals(toolTip))
					return true;
			} else {
				if (tips[i].contains(toolTip))
					return true;
			}
		}
		return false;
	}

	public boolean entityContainsLabel(Object id, String label,
			boolean blnExactMatch) {

		return entityContainsLabel(getEntityService().getEntity(id), label,
				blnExactMatch);
	}

	public boolean entityContainsLabel(Entity entity, String label,
			boolean blnExactMatch) {
		String[] ls = this.getEntityLabel(entity);

		for (int i = 0; i < ls.length; i++) {
			if (blnExactMatch) {
				if (ls[i].equals(label))
					return true;
			} else {
				if (ls[i].contains(label))
					return true;
			}
		}
		return false;
	}

	public boolean linkContainsLabel(Object id, String label,
			boolean blnExactMatch) {
		return linkContainsLabel(getEntityService().getLink(id), label,
				blnExactMatch);
	}

	public boolean linkContainsLabel(Link link, String label,
			boolean blnExactMatch) {
		String[] ls = this.getLinkLabel(link);
		for (int i = 0; i < ls.length; i++) {
			if (blnExactMatch) {
				if (ls[i].equals(label))
					return true;
			} else {
				if (ls[i].contains(label))
					return true;
			}
		}
		return false;
	}

	public Property getEntityDateTimeProperty(Entity entity) {
		EntityType et = getEntityType(entity);
		if (et == null)
			return null;

		String lnDateTime = et.getLNDateTime();

		return getEntityProperty(entity, lnDateTime);
	}

	public Property getEntityDateTimeProperty(Object id) {
		return getEntityDateTimeProperty(getEntityService().getEntity(id));
	}

	public CatProperty getEntityDateTimeCatProperty(Object id) {
		return getEntityDateTimeCatProperty(getEntityService().getEntity(id));
	}

	public CatProperty getEntityDateTimeCatProperty(Entity entity) {
		EntityType et = getEntityType(entity);
		if (et == null)
			return null;

		String lnDateTime = et.getLNDateTime();

		return getEntityCatProperty(entity, lnDateTime);
	}

	public Property getLinkDateTimeProperty(Link link) {
		LinkType lt = getLinkType(link);
		if (lt == null)
			return null;

		String lnDateTime = lt.getLndateTime();
		return getLinkProperty(link, lnDateTime);
	}
	
	public Date getEntityDateTime(Object id){
		return getEntityDateTime(getEntityService().getEntity(id));
	}
	
	public Date getEntityDateTime(Entity ent)
	{
		CatProperty catProperty = getEntityDateTimeCatProperty(ent);
		Property property = getEntityDateTimeProperty(ent);
		if (catProperty != null) {
			Form form = getTypeService()
			.getForm(catProperty.getFguid());
			if (form != null && property != null ) {
				return form.formatDate(property.getText());
			}
		}
		
		return null;
	}
	
	public Date getLinkDateTime(Object id){
		return getLinkDateTime(getEntityService().getLink(id));
	}
	
	public Date getLinkDateTime(Link link)
	{
		CatProperty catProperty = getLinkDateTimeCatProperty(link);
		Property property = getLinkDateTimeProperty(link);
		
		if( catProperty != null)
		{
			Form form = getTypeService()
			.getForm(catProperty.getFguid());
			if (form != null && property != null ) {
				return form.formatDate(property.getText());
			}
		}
		return null;
	}

	public Property getLinkDateTimeProperty(Object id) {
		return getLinkDateTimeProperty(getEntityService().getLink(id));
	}

	public CatProperty getLinkDateTimeCatProperty(Link link) {
		LinkType lt = getLinkType(link);
		if (lt == null)
			return null;

		String lnDateTime = lt.getLndateTime();
		return getLinkCatProperty(link, lnDateTime);
	}

	public CatProperty getLinkDateTimeCatProperty(Object id) {
		return getLinkDateTimeCatProperty(getEntityService().getLink(id));
	}
	
	public List<String> getThemedItemIds()
	{
		List<String> al = new ArrayList<String>();
		for( Entity entity : entityService.getEntities())
		{
			if( isThemedEntity(entity)){
				al.add(entity.getId());
			}
		}
		return al;
	}
	
	public boolean isThemedEntity( Object id)
	{
		return isThemedEntity(this.entityService.getEntity(id));
	}
	
	public boolean isThemedEntity(Entity ent)
	{
		EntityType et = getEntityType(ent);
		if( et != null) {
			if( Representation.THEME.equals(ent.getRepresentation()) )return true;
			else 
				return Representation.THEME.equals(et.getRepresentation());			
		}
		
		return false;
	}
	
	public List<Link> getLinks(String id1, String id2)
	{
		List<Link> al = new ArrayList<Link>();
		for( Link link : getEntityService().getLinks())
		{
			if(( link.getEnt1Id().equals(id1) && 
					link.getEnt2Id().equals(id2)) 
					|| (link.getEnt2Id().equals(id1) && link.getEnt1Id().equals(id2)))
				
			al.add(link);
		}
		return al;
	}
	
	public void setLinkShape(String link, String shape)
	{
		linkShape.put(link, shape);
	}
	
	public String getLinkShape(String link)
	{
		return linkShape.get(link);
	}
	
	public void lockEntity(String strEntity, boolean blnFix)
	{
		lockEntities.put(strEntity, blnFix);
	}
	
	public Map<Object,Boolean> getLockedEnds()
	{
		return lockEntities;
	}
}

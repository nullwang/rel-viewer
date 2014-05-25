package com.b.s;

import java.util.Collection;

import com.b.e.Entity;
import com.b.e.Link;

public interface EntityService {
	
	public void addLinks(Collection<Link> c	);
	
	public void addLink(Link link);
	
	public void addEntities(Collection<Entity> c);
	
	public void addEntity(Entity entity);
	
	public Entity getEntity(Object id);
	
	public Link getLink(Object id);
	
	public Collection<Entity> getEntities();
	
	public Collection<Link> getLinks();
	
	public void clear();
	
	public void removeEntities(Collection<Entity> c);
	
	public void removeEntity(Entity entity);
	
	public void removeEntity(Object id);
	
	public void removeLinks(Collection<Link> c);
	
	public void removeLink(Link link);
	
	public void removeLink(Object id);
	
	public void mergeEntityService(EntityService entityServcie);
	
	public boolean isLink(Object id);
	
	public boolean isEntity(Object id);
}

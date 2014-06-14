package rel.explorer.e;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class EntityAndLink {

	Map<Object, Entity> entities = new LinkedHashMap<Object, Entity>();
	Map<Object, Link> links = new LinkedHashMap<Object, Link>();

	public void addEntityAndLink(EntityAndLink eal)
	{
		if( eal == null )return;
		
		entities.putAll(eal.getEntities());
		links.putAll(eal.getLinks());
	}
	
	public void addEntity(Entity et) {
		entities.put(et.getId(), et);
	}

	public void addEntities(Collection<Entity> c) {
		for (Entity entity : c) {
			this.addEntity(entity);
		}
	}

	public void setEntities(Map<Object, Entity> entities) {
		this.entities = entities;
	}

	public void setLinks(Map<Object, Link> links) {
		this.links = links;
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

	public Map<Object, Entity> getEntities() {
		return entities;
	}

	public Map<Object, Link> getLinks() {
		return links;
	}

	public Link getLink(Object id) {
		return links.get(id);
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

	public boolean isEntity(Object id) {
		return entities.containsKey(id);
	}

	public boolean isLink(Object id) {
		return links.containsKey(id);
	}
}

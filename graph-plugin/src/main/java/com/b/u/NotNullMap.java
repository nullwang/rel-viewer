package com.b.u;

import java.util.HashMap;
import java.util.Map;

public class NotNullMap<K,V> extends HashMap<K,V> implements Map<K,V>{
	
	public NotNullMap()
	{
		super();
	}

	@Override
	 public V put(K key, V value) {
		if( key != null )
		return super.put(key, value);
		
		return null;
	}
}
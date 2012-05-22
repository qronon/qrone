package org.qrone.kvs;

import java.util.Hashtable;
import java.util.Map;

public class MemoryStore implements KeyValueStore{
	public Map<String, Object> map = new Hashtable<String, Object>();

	@Override
	public Object get(String key) {
		return map.get(key);
	}

	@Override
	public void set(String key, Object value) {
		map.put(key, value);
	}
	
	@Override
	public void set(String key, Object value, boolean weak) {
		map.put(key, value);
	}
}

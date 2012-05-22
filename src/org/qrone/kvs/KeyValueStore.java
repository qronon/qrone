package org.qrone.kvs;

public interface KeyValueStore {
	public Object get(String key);
	public void set(String key, Object value);
	public void set(String key, Object value, boolean weak);
}

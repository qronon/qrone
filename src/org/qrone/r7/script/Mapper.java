package org.qrone.r7.script;

public interface Mapper {
	public boolean exist(String key);
	public void put(String key, Object value);
	public Object get(String key);
	public Object remove(String key);
}

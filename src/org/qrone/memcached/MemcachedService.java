package org.qrone.memcached;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public interface MemcachedService {
	public void clearAll();
	public boolean contains(Object key);
	public boolean delete(Object key);
	public boolean delete(Object key, long millisNoReAdd);
	public Set<Object> deleteAll(Collection<Object> keys);
	public Set<Object> deleteAll(Collection<Object> keys, long millisNoReAdd);
	public Object get(Object key);
	public Map<Object, Object> getAll(Collection<Object> keys);
	public String getNamespace();
	public long increment(Object key, long delta);
	public void put(Object key, Object value);
	public void put(Object key, Object value, int ttlmillis);
	public void put(Object key, Object value, Date expire);
	public void put(Object key, Object value, int ttlmillis, SetPolicy polocy);
	public void put(Object key, Object value, Date expire, SetPolicy polocy);
	public void putAll(Map<Object, Object> values);
	public void putAll(Map<Object, Object> values, int ttlmillis);
	public void putAll(Map<Object, Object> values, Date expire);
	public void putAll(Map<Object, Object> values, int ttlmillis, SetPolicy polocy);
	public void putAll(Map<Object, Object> values, Date expire, SetPolicy polocy);
	public void setNamespace(String newNamespace);
	
	public enum SetPolicy{
		SET_ALWAYS,
		REPLACE_ONLY_IF_PRESENT ,
		ADD_ONLY_IF_NOT_PRESENT
	}
}

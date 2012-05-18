package org.qrone.r7.script.ext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mozilla.javascript.Scriptable;

public class ObjectMap implements Map{
	private Scriptable scr;
	private Set keys;
	private List values;
	public ObjectMap(Scriptable scr){
		this.scr = scr;
		Object[] ids = scr.getIds();
		keys = new HashSet();
		for (int i = 0; i < ids.length; i++) {
			keys.add(ids[i]);
		}
	}
	
	public static Map from(Object obj){
		if(obj instanceof Scriptable){
			return new ObjectMap((Scriptable)obj);
		}
		throw new IllegalArgumentException();
	}

	@Override
	public int size() {
		return keys.size();
	}

	@Override
	public boolean isEmpty() {
		return keys.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return keys.contains(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return values.contains(value);
	}

	@Override
	public Object get(Object key) {
		return scr.get(key.toString(), scr);
	}

	@Override
	public Object put(Object key, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set keySet() {
		return keys;
	}

	@Override
	public Collection values() {
		if(values != null) return values;
		values = new ArrayList();
		for (Object key : keys) {
			values.add(scr.get(key.toString(), scr));
		}
		return values;
	}

	@Override
	public Set entrySet() {
		Set l = new HashSet();
		for (final Object key : l) {
			l.add(new Map.Entry() {
				@Override
				public Object getKey() {
					return key;
				}

				@Override
				public Object getValue() {
					return scr.get(key.toString(), scr);
				}

				@Override
				public Object setValue(Object value) {
					throw new UnsupportedOperationException();
				}
			});
		}
		return l;
	}

}

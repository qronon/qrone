package org.qrone.r7.store;

import java.util.Hashtable;
import java.util.Map;

public class MemoryStore implements KeyValueStore{
	public static Map<String, byte[]> map = new Hashtable<String, byte[]>();

	@Override
	public byte[] get(String key) {
		return map.get(key);
	}

	@Override
	public void set(String key, byte[] value) {
		map.put(key, value);
	}

	@Override
	public void set(String key, byte[] value, long expire) {
		map.put(key, value);
	}

}

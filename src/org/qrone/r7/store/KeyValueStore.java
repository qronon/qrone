package org.qrone.r7.store;

public interface KeyValueStore {
	public byte[] get(String key);
	public void set(String key, byte[] value);
	public void set(String key, byte[] value, long expire);
}

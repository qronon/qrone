package org.qrone.kvs;

public interface KeyValueStore {
	public byte[] get(String key);
	public void set(String key, byte[] value);
	public void set(String key, byte[] value, boolean weak);
}

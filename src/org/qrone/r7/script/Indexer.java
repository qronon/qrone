package org.qrone.r7.script;

public interface Indexer {
	public boolean exist(int index);
	public void put(int index, Object value);
	public Object get(int index);
	public Object remove(int index);
	public int size();
}

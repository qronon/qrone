package org.qrone.kvs;

import org.mozilla.javascript.Scriptable;

public interface KVSTable {
	public KVSCursor find();
	public KVSCursor find(Scriptable o);
	public KVSCursor find(Scriptable o, Scriptable o2);

	public void remove(Scriptable o);
	public void insert(Scriptable o);
	public void save(Scriptable o);
	public void drop();

}

package org.qrone.kvs;

import org.mozilla.javascript.Scriptable;

public interface KVSTable {
	public KVSCursor find();
	public KVSCursor find(Scriptable o);
	public KVSCursor find(Scriptable o, Scriptable p);
	public KVSCursor find(Scriptable o, Scriptable p, Number skip);
	public KVSCursor find(Scriptable o, Scriptable p, Number skip, Number limit);

	public void remove(Scriptable o);
	public void insert(Scriptable o);
	public void save(Scriptable o);
	public void drop();

}

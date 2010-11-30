package org.qrone.database;

import org.mozilla.javascript.Scriptable;

public interface DatabaseTable {
	public DatabaseCursor find();
	public DatabaseCursor find(Scriptable o);
	public DatabaseCursor find(Scriptable o, Scriptable p);
	public DatabaseCursor find(Scriptable o, Scriptable p, Number skip);
	public DatabaseCursor find(Scriptable o, Scriptable p, Number skip, Number limit);

	public void remove(Scriptable o);
	public void insert(Scriptable o);
	public void save(Scriptable o);
	public void drop();

}

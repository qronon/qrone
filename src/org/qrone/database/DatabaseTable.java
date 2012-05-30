package org.qrone.database;

import java.util.Map;

import org.mozilla.javascript.Scriptable;

public interface DatabaseTable {
	public DatabaseCursor find();
	public DatabaseCursor find(Map o);
	
	public String insert(Map o);
	public String save(Map o);
	public void remove(Map o);
	public void remove(String id);
	
	public void drop();
}

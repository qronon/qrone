package org.qrone.database;

import java.util.Map;

import org.mozilla.javascript.Scriptable;

public interface DatabaseTable {
	public DatabaseCursor find();
	public DatabaseCursor find(Scriptable o);
	public DatabaseCursor find(Scriptable o, Scriptable p);
	public DatabaseCursor find(Scriptable o, Scriptable p, Number skip);
	public DatabaseCursor find(Scriptable o, Scriptable p, Number skip, Number limit);
	public DatabaseCursor find(Map o);
	public DatabaseCursor find(Map o, Map p);
	public DatabaseCursor find(Map o, Map p, Number skip);
	public DatabaseCursor find(Map o, Map p, Number skip, Number limit);

	public void remove(Scriptable o);
	public void remove(Map o);
	public void remove(String id);
	
	public String insert(Scriptable o);
	public String insert(Map o);
	
	public String save(Scriptable o);
	public String save(Map o);
	
	public void drop();
	
	public String update(Scriptable o, Scriptable o2);
	public String update(Map o, Map o2);

}

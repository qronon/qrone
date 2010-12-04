package org.qrone.database;

import java.util.Map;

import org.mozilla.javascript.Scriptable;
import org.qrone.r7.script.browser.Function;

public interface DatabaseCursor {
	public DatabaseCursor skip(Number o);
	public DatabaseCursor limit(Number o);
	public DatabaseCursor sort(Scriptable o);
	
	public boolean hasNext();
	public Map next();
	public void forEach(Function func);
	
}

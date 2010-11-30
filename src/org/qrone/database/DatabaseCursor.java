package org.qrone.database;

import org.mozilla.javascript.Scriptable;
import org.qrone.r7.script.browser.Function;

public interface DatabaseCursor {
	public DatabaseCursor skip(Number o);
	public DatabaseCursor limit(Number o);
	public DatabaseCursor sort(Scriptable o);
	
	public boolean hasNext();
	public Object next();
	public void forEach(Function func);
	
}

package org.qrone.database;

import java.util.Map;

import org.mozilla.javascript.Scriptable;
import org.qrone.r7.script.browser.Function;

public interface DatabaseCursor {
	public boolean hasNext();
	public Map next();
	
}

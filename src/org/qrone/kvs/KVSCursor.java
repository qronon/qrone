package org.qrone.kvs;

import org.mozilla.javascript.Scriptable;
import org.qrone.r7.script.browser.Function;

public interface KVSCursor {
	public KVSCursor skip(Number o);
	public KVSCursor limit(Number o);
	public KVSCursor sort(Scriptable o);
	
	public boolean hasNext();
	public Object next();
	public void forEach(Function func);
	
}

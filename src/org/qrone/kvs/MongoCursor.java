package org.qrone.kvs;

import org.bson.BSONObject;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Scriptable;
import org.qrone.r7.ObjectConverter;
import org.qrone.r7.script.ServletScopeObject;
import org.qrone.r7.script.ServletScope;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MongoCursor extends ServletScopeObject implements KVSCursor {
	private DBCursor c;
	public MongoCursor(ServletScope ss, DBCursor c) {
		super(ss);
		this.c = c;
	}
	
	@Override
	public void forEach(Callable func) {
		while(hasNext()){
			callJSFunction(func, this, next());
		}
	}
	
	@Override
	public boolean hasNext() {
		return c.hasNext();
	}
	@Override
	public KVSCursor limit(Number o) {
		return new MongoCursor(ss, c.limit(o.intValue()));
	}
	@Override
	public Object next() {
		return ObjectConverter.from(c.next());
	}
	@Override
	public KVSCursor skip(Number o) {
		return new MongoCursor(ss, c.skip(o.intValue()));
	}
	
	@Override
	public KVSCursor sort(Scriptable o) {
		return new MongoCursor(ss, c.sort((DBObject)ObjectConverter.to(o)));
	}

}

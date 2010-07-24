package org.qrone.kvs;

import org.bson.BSON;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.qrone.r7.script.JSObject;
import org.qrone.r7.script.ServletScope;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class MongoCursor extends JSObject implements KVSCursor {
	private DBCursor c;
	public MongoCursor(ServletScope ss, DBCursor c) {
		super(ss);
		this.c = c;
	}
	@Override
	public void forEach(FunctionObject func) {
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
		return c.next();
	}
	@Override
	public KVSCursor skip(Number o) {
		return new MongoCursor(ss, c.skip(o.intValue()));
	}
	@Override
	public KVSCursor sort(Scriptable o) {
		return new MongoCursor(ss, c.sort(BSONConverter.to(o)));
	}

}

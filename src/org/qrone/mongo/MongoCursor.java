package org.qrone.mongo;

import java.util.Map;

import org.qrone.database.DatabaseCursor;
import org.qrone.r7.script.Scriptables;
import org.qrone.r7.script.browser.Function;
import org.qrone.r7.script.ext.ScriptablePrototype;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

public class MongoCursor implements ScriptablePrototype<DBCursor>, DatabaseCursor {
	private DBCursor c;
	public MongoCursor( DBCursor c) {
		this.c = c;
	}
	 
	public void forEach(Function func) {
		while(hasNext()){
			func.call(next());
		}
	}
	
	@Override
	public boolean hasNext() {
		return c.hasNext();
	}
	
	@Override
	public Map next() {
		if(c.hasNext())
			return c.next().toMap();
		return null;
	}

	public MongoCursor limit(Number o) {
		return new MongoCursor(c.limit(o.intValue()));
	}
	
	public MongoCursor skip(Number o) {
		return new MongoCursor(c.skip(o.intValue()));
	}
	

	public MongoCursor sort(Map o) {
		return new MongoCursor(c.sort(new BasicDBObject(o)));
	}

	public MongoCursor sort(Object o) {
		return sort(Scriptables.asMap(o));
	}

}

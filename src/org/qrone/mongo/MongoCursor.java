package org.qrone.mongo;

import java.util.Map;
import java.util.NoSuchElementException;

import org.mozilla.javascript.Scriptable;
import org.qrone.database.DatabaseCursor;
import org.qrone.r7.app.ObjectConverter;
import org.qrone.r7.script.ScriptablePrototype;
import org.qrone.r7.script.browser.Function;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MongoCursor implements ScriptablePrototype<DBCursor>, DatabaseCursor {
	private DBCursor c;
	public MongoCursor( DBCursor c) {
		this.c = c;
	}
	 
	@Override
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
	public DatabaseCursor limit(Number o) {
		return new MongoCursor(c.limit(o.intValue()));
	}
	@Override
	public Map next() {
		if(c.hasNext())
			return c.next().toMap();
		return null;
	}
	@Override
	public DatabaseCursor skip(Number o) {
		return new MongoCursor(c.skip(o.intValue()));
	}
	
	@Override
	public DatabaseCursor sort(Scriptable o) {
		return new MongoCursor(c.sort((DBObject)ObjectConverter.to(o)));
	}

	@Override
	public DatabaseCursor sort(Map o) {
		// TODO Auto-generated method stub
		return null;
	}

}

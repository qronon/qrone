package org.qrone.kvs;

import org.mozilla.javascript.Scriptable;
import org.qrone.r7.script.JSObject;
import org.qrone.r7.script.ServletScope;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class MongoTable extends JSObject implements KVSTable {
	private DBCollection coll;
	public MongoTable(ServletScope ss, DBCollection coll) {
		super(ss);
		this.coll = coll;
	}

	@Override
	public void drop() {
		coll.drop();
	}

	@Override
	public KVSCursor find() {
		return new MongoCursor(ss, coll.find());
	}

	@Override
	public KVSCursor find(Scriptable o) {
		return new MongoCursor(ss, coll.find((DBObject)BSONConverter.to(o)));
	}

	@Override
	public KVSCursor find(Scriptable o, Scriptable o2) {
		return new MongoCursor(ss, coll.find((DBObject)BSONConverter.to(o), (DBObject)BSONConverter.to(o2)));
	}

	@Override
	public void insert(Scriptable o) {
		coll.insert((DBObject)BSONConverter.to(o));
	}

	@Override
	public void remove(Scriptable o) {
		coll.remove((DBObject)BSONConverter.to(o));
	}

	@Override
	public void save(Scriptable o) {
		coll.save((DBObject)BSONConverter.to(o));
	}

}

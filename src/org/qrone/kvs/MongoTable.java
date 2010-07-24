package org.qrone.kvs;

import org.bson.BSON;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.qrone.r7.script.JSObject;
import org.qrone.r7.script.ServletScope;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

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
		return new MongoCursor(ss, coll.find(BSONConverter.to(o)));
	}

	@Override
	public KVSCursor find(Scriptable o, Scriptable o2) {
		return new MongoCursor(ss, coll.find(BSONConverter.to(o), BSONConverter.to(o2)));
	}

	@Override
	public void insert(Scriptable o) {
		coll.insert(BSONConverter.to(o));
	}

	@Override
	public void remove(Scriptable o) {
		coll.remove(BSONConverter.to(o));
	}

	@Override
	public void save(Scriptable o) {
		coll.save(BSONConverter.to(o));
	}

}

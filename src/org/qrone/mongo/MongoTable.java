package org.qrone.mongo;

import java.util.Map;

import org.mozilla.javascript.Scriptable;
import org.qrone.database.DatabaseCursor;
import org.qrone.database.DatabaseTable;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class MongoTable implements DatabaseTable {
	private DBCollection coll;
	public MongoTable(DBCollection coll) {
		this.coll = coll;
	}
	

	@Override
	public void remove(Scriptable o) {
		coll.remove((DBObject)BsonUtil.to(o));
	}

	@Override
	public String update(Map o, Map o2) {
		coll.update(new BasicDBObject(o),new BasicDBObject(o2));
		return null;
	}
	
	@Override
	public String update(Scriptable o, Scriptable o2) {
		coll.update((DBObject)BsonUtil.to(o),(DBObject)BsonUtil.to(o2));
		return null;
	}
	
	@Override
	public String save(Map o) {
		coll.save(new BasicDBObject(o));
		return null;
	}
	
	@Override
	public String save(Scriptable o) {
		coll.save((DBObject)BsonUtil.to(o));
		return null;
	}

	@Override
	public void drop() {
		coll.drop();
	}

	@Override
	public String insert(Scriptable o) {
		return save(o);
	}
	
	@Override
	public String insert(Map o) {
		return save(o);
	}

	@Override
	public DatabaseCursor find() {
		return new MongoCursor(coll.find());
	}

	@Override
	public DatabaseCursor find(Scriptable o) {
		return new MongoCursor(coll.find((DBObject)BsonUtil.to(o)));
	}
	
	@Override
	public DatabaseCursor find(Scriptable o, Scriptable p) {
		return new MongoCursor(coll.find((DBObject)BsonUtil.to(o),
				(DBObject)BsonUtil.to(p)));
	}

	@Override
	public DatabaseCursor find(Scriptable o, Scriptable p, Number skip) {
		return find(o, p).skip(skip);
	}

	@Override
	public DatabaseCursor find(Scriptable o, Scriptable p, Number skip, Number limit) {
		return find(o, p).skip(skip).limit(limit);
	}

	@Override
	public DatabaseCursor find(Map o) {
		return new MongoCursor(coll.find(new BasicDBObject(o)));
	}

	@Override
	public DatabaseCursor find(Map o, Map p) {
		return new MongoCursor(coll.find(new BasicDBObject(o),new BasicDBObject(p)));
	}

	@Override
	public DatabaseCursor find(Map o, Map p, Number skip) {
		return find(o, p).skip(skip);
	}

	@Override
	public DatabaseCursor find(Map o, Map p, Number skip, Number limit) {
		return find(o, p).skip(skip).limit(limit);
	}

	@Override
	public void remove(Map o) {
		coll.remove(new BasicDBObject(o));
	}

	@Override
	public void remove(String id) {
		
	}

}

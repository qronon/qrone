package org.qrone.mongo;

import java.util.Map;

import org.qrone.database.DatabaseTable;
import org.qrone.r7.script.Scriptables;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class MongoTable implements DatabaseTable {
	private DBCollection coll;
	public MongoTable(DBCollection coll) {
		this.coll = coll;
	}
	
	
	public String update(Object o, Object o2) {
		return update(Scriptables.asMap(o), Scriptables.asMap(o2));
	}

	public String update(Map o, Map o2) {
		BasicDBObject dbo = new BasicDBObject(o);
		coll.update(dbo,new BasicDBObject(o2));
		return dbo.get("_id").toString();
	}
	
	public String save(Object o) {
		return save(Scriptables.asMap(o));
	}

	@Override
	public String save(Map o) {
		BasicDBObject dbo = new BasicDBObject(o);
		coll.save(dbo);
		return dbo.get("_id").toString();
	}

	public String insert(Object o) {
		return insert(Scriptables.asMap(o));
	}
	
	@Override
	public String insert(Map o) {
		BasicDBObject dbo = new BasicDBObject(o);
		coll.insert(dbo);
		return dbo.get("_id").toString();
	}

	@Override
	public MongoCursor find() {
		return new MongoCursor(coll.find());
	}

	public MongoCursor find(Object o) {
		return find(Scriptables.asMap(o));
	}

	@Override
	public MongoCursor find(Map o) {
		return new MongoCursor(coll.find(new BasicDBObject(o)));
	}
	
	public MongoCursor find(Object o, Object p) {
		return find(Scriptables.asMap(o),Scriptables.asMap(p));
	}

	public MongoCursor find(Map o, Map p) {
		return new MongoCursor(coll.find(new BasicDBObject(o),new BasicDBObject(p)));
	}

	public void remove(Object o) {
		remove(Scriptables.asMap(o));
	}
	
	@Override
	public void remove(Map o) {
		coll.remove(new BasicDBObject(o));
	}

	@Override
	public void remove(String id) {
		coll.remove(new BasicDBObject("_id", id));
	}
	
	@Override
	public void drop() {
		coll.drop();
	}

}

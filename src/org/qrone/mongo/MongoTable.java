package org.qrone.mongo;

import java.util.Map;

import org.qrone.database.DatabaseTable;
import org.qrone.r7.script.Scriptables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class MongoTable implements DatabaseTable {
	private static Logger log = LoggerFactory.getLogger(MongoTable.class);
	
	private DBCollection coll;
	public MongoTable(DBCollection coll) {
		this.coll = coll;
	}
	
	
	public String update(Object o, Object o2) {
		return update(Scriptables.asMap(o), Scriptables.asMap(o2));
	}

	public String update(Map o, Map o2) {
		BasicDBObject dbo = new BasicDBObject(o);
		log.debug("Mongod UPDATE (collection:{})", coll);
		coll.update(dbo,new BasicDBObject(o2));
		return dbo.get("_id").toString();
	}
	
	public String save(Object o) {
		return save(Scriptables.asMap(o));
	}

	@Override
	public String save(Map o) {
		BasicDBObject dbo = new BasicDBObject(o);
		log.debug("Mongod SAVE (collection:{})", coll);
		coll.save(dbo);
		return dbo.get("_id").toString();
	}

	public String insert(Object o) {
		return insert(Scriptables.asMap(o));
	}
	
	@Override
	public String insert(Map o) {
		BasicDBObject dbo = new BasicDBObject(o);
		log.debug("Mongod INSERT (collection:{})", coll);
		coll.insert(dbo);
		return dbo.get("_id").toString();
	}

	@Override
	public MongoCursor find() {
		log.debug("Mongod FIND (collection:{})", coll);
		return new MongoCursor(coll.find());
	}

	public MongoCursor find(Object o) {
		return find(Scriptables.asMap(o));
	}

	@Override
	public MongoCursor find(Map o) {
		log.debug("Mongod FIND (collection:{})", coll);
		return new MongoCursor(coll.find(new BasicDBObject(o)));
	}
	
	public MongoCursor find(Object o, Object p) {
		return find(Scriptables.asMap(o),Scriptables.asMap(p));
	}

	public MongoCursor find(Map o, Map p) {
		log.debug("Mongod FIND (collection:{})", coll);
		return new MongoCursor(coll.find(new BasicDBObject(o),new BasicDBObject(p)));
	}

	public void remove(Object o) {
		remove(Scriptables.asMap(o));
	}
	
	@Override
	public void remove(Map o) {
		log.debug("Mongod REMOVE (collection:{})", coll);
		coll.remove(new BasicDBObject(o));
	}

	@Override
	public void remove(String id) {
		log.debug("Mongod REMOVE (collection:{})", coll);
		coll.remove(new BasicDBObject("_id", id));
	}
	
	@Override
	public void drop() {
		log.debug("Mongod DROP (collection:{})", coll);
		coll.drop();
	}

}

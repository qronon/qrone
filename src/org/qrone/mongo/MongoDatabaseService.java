package org.qrone.mongo;

import java.util.Hashtable;
import java.util.Map;

import org.mozilla.javascript.Scriptable;
import org.qrone.database.DatabaseService;
import org.qrone.database.DatabaseTable;
import org.qrone.r7.script.AbstractScriptable;

import com.mongodb.DB;

public class MongoDatabaseService extends AbstractScriptable implements DatabaseService{
	private String domain;
	private DB db;
	private Map<String, MongoTable> map = new Hashtable<String, MongoTable>();

	public MongoDatabaseService(DB db, String domain){
		this.db = db;
		this.domain = domain;
	}
	
	@Override
	public DatabaseTable getCollection(String name) {
		MongoTable t = map.get(name);
		if(t == null){
			t = new MongoTable(db.getCollection(domain + "/" + name));
			map.put(name, t);
		}
		return t;
	}

	@Override
	public Object get(String key, Scriptable start) {
		return getCollection(key);
	}

	@Override
	public Object[] getIds() {
		return map.keySet().toArray(new Object[map.size()]);
	}
}

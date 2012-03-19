package org.qrone.mongo;

import java.util.Hashtable;
import java.util.Map;

import org.mozilla.javascript.Scriptable;
import org.qrone.database.DatabaseService;
import org.qrone.database.DatabaseTable;
import org.qrone.r7.script.AbstractScriptable;

import com.mongodb.DB;

public class MongoDatabaseService extends AbstractScriptable implements DatabaseService{
	private DB db;
	private Map<String, MongoTable> map = new Hashtable<String, MongoTable>();

	public MongoDatabaseService(DB db){
		this.db = db;
	}
	
	@Override
	public DatabaseTable getCollection(String name) {
		MongoTable t = map.get(name);
		if(t == null){
			t = new MongoTable(db.getCollection(name));
		}
		return t;
	}

	@Override
	public Object get(String key, Scriptable start) {
		if(map.containsKey(key)){
			return map.get(key);
		}
		
		DatabaseTable table = getCollection(key);
		map.put(key, (MongoTable)table);
		return table;
	}

	@Override
	public Object[] getIds() {
		return map.keySet().toArray(new Object[map.size()]);
	}
}

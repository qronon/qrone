package org.qrone.kvs;

import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Map;

import org.mozilla.javascript.Scriptable;
import org.qrone.r7.script.JSObject;
import org.qrone.r7.script.JSScriptable;
import org.qrone.r7.script.ServletScope;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class MongoService extends JSScriptable implements KVSService{
	private Mongo mongo;
	private String schema;
	private DB db;
	private Map<String, MongoTable> map = new Hashtable<String, MongoTable>();

	public MongoService(ServletScope ss, String host, String schema) throws UnknownHostException, MongoException {
		this(ss, new Mongo(host, 27017), schema);
	}
	
	public MongoService(ServletScope ss, String host, String schema, 
			String user, String password) throws UnknownHostException, MongoException {
		this(ss, new Mongo(host, 27017), schema);
		db.authenticate(user, password.toCharArray());
	}
	
	public MongoService(ServletScope ss, Mongo mongo, String schema) {
		super(ss);
		this.mongo = mongo;
		this.schema = schema;
		db = mongo.getDB(schema);
	}
	
	@Override
	public KVSTable getCollection(String name) {
		MongoTable t = map.get(name);
		if(t == null){
			t = new MongoTable(ss, db.getCollection(name));
		}
		return t;
	}

	@Override
	public Object get(String arg0, Scriptable arg1) {
		return getCollection(arg0);
	}

	@Override
	public Object[] getIds() {
		return map.keySet().toArray(new Object[map.size()]);
	}

}

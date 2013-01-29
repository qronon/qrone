package org.qrone.kvs;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mozilla.javascript.Scriptable;
import org.qrone.database.DatabaseCursor;
import org.qrone.database.DatabaseService;
import org.qrone.database.DatabaseTable;
import org.qrone.memcached.Memcached;
import org.qrone.memcached.MemcachedService;
import org.qrone.r7.script.AbstractScriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalKeyValueStoreService extends AbstractScriptable implements KeyValueStoreService{
	private static Logger log = LoggerFactory.getLogger(LocalKeyValueStoreService.class);

	private DatabaseService db;
	private MemcachedService mems;
	
	private Set<String> list = new HashSet<String>();
	
	public LocalKeyValueStoreService(DatabaseService db, MemcachedService mem){
		this.db = db;
		this.mems = mem;
	}
	
	@Override
	public KeyValueStore getKeyValueStore(String collection) {
		return new LocalKeyValueStore(db, mems, collection);
	}
	
	public static class LocalKeyValueStore extends AbstractScriptable implements KeyValueStore {
		private DatabaseTable table;
		private Memcached mem;
		public LocalKeyValueStore(DatabaseService db, MemcachedService mems, String collection){
			table = db.getCollection(collection);
			mem = mems.getKeyValueStore(collection);
		}
		
		@Override
		public Object get(String key) {
			Object v;
			v = mem.get(key);
			if(v != null){
				log.debug("KVS Memcached READ (key:{})", key);
				return v;
			}

			Map map = new HashMap();
			map.put("_id", key);
			DatabaseCursor cursor = table.find(map);
			Map result = cursor.next();
			if(result == null) return null;
			v = result.get("value");
			if(v != null){
				log.debug("KVS Database READ (key:{})", key);
				return v;
			}
			return null;
		}

		@Override
		public void set(String key, Object value) {
			mem.put(key, value);
			
			Map map = new HashMap();
			map.put("_id", key);
			map.put("value", value);
			table.save(map);

			log.debug("KVS Database WRITE (key:{})", key);
		}

		@Override
		public void set(String key, Object value, boolean weak) {
			if(weak){
				mem.put(key, value);
			}else{
				set(key, value);
			}
		}

		@Override
		public Object get(String key, Scriptable s) {
			return get(key);
		}

		@Override
		public Object[] getIds() {
			return new Object[0];
		}

		@Override
		public void put(String key, Scriptable s, Object value) {
			set(key, value);
		}
		
	}

	@Override
	public Object get(String collection, Scriptable s) {
		list.add(collection);
		return getKeyValueStore(collection);
	}

	@Override
	public Object[] getIds() {
		return list.toArray(new Object[list.size()]);
	}
	
}

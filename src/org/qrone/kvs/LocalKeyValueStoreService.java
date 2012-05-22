package org.qrone.kvs;


import java.util.HashMap;
import java.util.Map;

import org.qrone.database.DatabaseCursor;
import org.qrone.database.DatabaseService;
import org.qrone.database.DatabaseTable;
import org.qrone.memcached.Memcached;
import org.qrone.memcached.MemcachedService;

public class LocalKeyValueStoreService implements KeyValueStoreService{

	private DatabaseService db;
	private MemcachedService mems;
	
	public LocalKeyValueStoreService(DatabaseService db, MemcachedService mem){
		this.db = db;
		this.mems = mem;
	}
	
	@Override
	public KeyValueStore getKeyValueStore(String collection) {
		return new LocalKeyValueStore(db, mems, collection);
	}
	
	public static class LocalKeyValueStore implements KeyValueStore {
		private DatabaseTable table;
		private Memcached mem;
		public LocalKeyValueStore(DatabaseService db, MemcachedService mems, String collection){
			table = db.getCollection(collection);
			mem = mems.getKeyValueStore(collection);
		}
		
		@Override
		public byte[] get(String key) {
			Object v;
			v = mem.get(key);
			if(v != null & v instanceof byte[]){
				return (byte[])v;
			}

			Map map = new HashMap();
			map.put("id", key);
			DatabaseCursor cursor = table.find(map);
			Map result = cursor.next();
			if(result == null) return null;
			v = result.get(key);
			if(v != null & v instanceof byte[]){
				return (byte[])v;
			}
			return null;
		}

		@Override
		public void set(String key, Object value) {
			mem.put(key, value);
			
			Map map = new HashMap();
			map.put("id", key);
			map.put("value", value);
			table.save(map);
		}

		@Override
		public void set(String key, Object value, boolean weak) {
			if(weak){
				mem.put(key, value);
			}else{
				set(key, value);
			}
		}
		
	}
	
}

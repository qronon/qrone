package org.qrone.memcached;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class LocalMemcachedService implements MemcachedService{
	private static SockIOPool pool;
	private MemCachedClient client;
	
	public LocalMemcachedService(String[] serverlist) {
		if (pool == null) {
			SockIOPool pool = SockIOPool.getInstance();
			pool.setServers(serverlist);
			pool.initialize();
		}
		client = new MemCachedClient();
	}
	
	@Override
	public Memcached getKeyValueStore(String collection) {
		// TODO Auto-generated method stub
		return new LocalMemcached(client, collection);
	}
	
	public static class LocalMemcached implements Memcached{
		private String collection;
		private MemCachedClient client;
		
		public LocalMemcached(MemCachedClient client, String collection) {
			this.collection = collection;
			this.client = client;
		}
		
		@Override
		public void clearAll() {
			client.flushAll();
		}

		@Override
		public boolean contains(String key) {
			return client.keyExists(collection + key);
		}

		@Override
		public boolean delete(String key) {
			return client.delete(collection + key);
		}

		@Override
		public boolean delete(String key, long millisNoReAdd) {
			return client.delete(collection + key, new Date(System.currentTimeMillis()+millisNoReAdd));
		}

		@Override
		public Set<String> deleteAll(Collection<String> keys) {
			Set<String> set = new HashSet<String>();
			for (Iterator<String> i = keys.iterator(); i.hasNext();) {
				String string = i.next();
				if(delete(string))
					set.add(string);
			}
			return set;
		}

		@Override
		public Set<String> deleteAll(Collection<String> keys, long millisNoReAdd) {
			Set<String> set = new HashSet<String>();
			for (Iterator<String> i = keys.iterator(); i.hasNext();) {
				String string = i.next();
				if(delete(string,millisNoReAdd))
					set.add(string);
			}
			return set;
		}

		@Override
		public Object get(String key) {
			return client.get(collection + key);
		}

		@Override
		public Map<String, Object> getAll(Collection<String> keys) {
			String[] keysori = keys.toArray(new String[keys.size()]);
			String[] keysary = new String[keys.size()];
			for (int i = 0; i < keysary.length; i++) {
				keysary[i] = collection + keysori[i];
			}
			return client.getMulti(keysary);
		}

		@Override
		public long increment(String key, long delta) {
			return client.incr(collection + key, delta);
		}

		@Override
		public void put(String key, Object value) {
			client.add(collection + key, value);
		}

		@Override
		public void put(String key, Object value, int ttlmillis) {
			client.add(collection + key, value, new Date(System.currentTimeMillis()+ttlmillis));
		}

		@Override
		public void put(String key, Object value, Date expire) {
			client.add(collection + key, value, expire);
		}

		@Override
		public void put(String key, Object value, int ttlmillis, SetPolicy policy) {
			put(key, value, new Date(System.currentTimeMillis()+ttlmillis), policy);
		}

		@Override
		public void put(String key, Object value, Date expire, SetPolicy policy) {
			if(policy == SetPolicy.SET_ALWAYS){
				client.add(collection + key, value, expire);
			}else if(policy == SetPolicy.REPLACE_ONLY_IF_PRESENT){
				client.replace(collection + key, value, expire);
			}else if(policy == SetPolicy.ADD_ONLY_IF_NOT_PRESENT){
				if(!client.keyExists(collection + key))
					client.add(collection + key, value, expire);
			}
		}

		@Override
		public void putAll(Map<String, Object> values) {
			for (Iterator<Entry<String, Object>> i = values.entrySet().iterator(); i
					.hasNext();) {
				Entry<String, Object> v = i.next();
				put(v.getKey(),v.getValue());
			}
		}

		@Override
		public void putAll(Map<String, Object> values, int ttlmillis) {
			for (Iterator<Entry<String, Object>> i = values.entrySet().iterator(); i
					.hasNext();) {
				Entry<String, Object> v = i.next();
				put(v.getKey(),v.getValue(),ttlmillis);
			}
		}

		@Override
		public void putAll(Map<String, Object> values, Date expire) {
			for (Iterator<Entry<String, Object>> i = values.entrySet().iterator(); i
					.hasNext();) {
				Entry<String, Object> v = i.next();
				put(v.getKey(),v.getValue(),expire);
			}
		}

		@Override
		public void putAll(Map<String, Object> values, int ttlmillis,
				SetPolicy policy) {
			for (Iterator<Entry<String, Object>> i = values.entrySet().iterator(); i
					.hasNext();) {
				Entry<String, Object> v = i.next();
				put(v.getKey(),v.getValue(),ttlmillis, policy);
			}
		}

		@Override
		public void putAll(Map<String, Object> values, Date expire, SetPolicy policy) {
			for (Iterator<Entry<String, Object>> i = values.entrySet().iterator(); i
					.hasNext();) {
				Entry<String, Object> v = i.next();
				put(v.getKey(),v.getValue(),expire, policy);
			}
		}
	}

}

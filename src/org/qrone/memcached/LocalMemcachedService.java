package org.qrone.memcached;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.mozilla.javascript.Scriptable;
import org.qrone.mongo.MongoTable;
import org.qrone.r7.script.AbstractScriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class LocalMemcachedService extends AbstractScriptable implements MemcachedService{
	private static Logger log = LoggerFactory.getLogger(LocalMemcachedService.class);
	private static SockIOPool pool;
	
	private String domain;
	private MemCachedClient client;
	private Map<String, LocalMemcached> map = new Hashtable<String, LocalMemcached>();
	
	public LocalMemcachedService(String[] serverlist, String domain) {
		if (pool == null) {
			SockIOPool pool = SockIOPool.getInstance();
			pool.setServers(serverlist);
			pool.initialize();
		}
		client = new MemCachedClient();
		this.domain = domain;
	}
	
	@Override
	public Memcached getKeyValueStore(String name) {
		LocalMemcached t = map.get(name);
		if(t == null){
			t = new LocalMemcached(client, domain + "/" + name);
			map.put(name, t);
		}
		return t;
	}

	@Override
	public Object get(String key, Scriptable start) {
		return getKeyValueStore(key);
	}

	@Override
	public Object[] getIds() {
		return map.keySet().toArray(new Object[map.size()]);
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
			log.debug("Memcached FLUSHALL (collection:{})", collection);
			client.flushAll();
		}

		@Override
		public boolean contains(String key) {
			log.debug("Memcached KEYEXISTS (collection:{} key:{})", collection, key);
			return client.keyExists(collection + key);
		}

		@Override
		public boolean remove(String key) {
			log.debug("Memcached DELETE (collection:{} key:{})", collection, key);
			return client.delete(collection + key);
		}

		@Override
		public boolean remove(String key, long millisNoReAdd) {
			log.debug("Memcached DELETE (collection:{} key:{})", collection, key);
			return client.delete(collection + key, new Date(System.currentTimeMillis()+millisNoReAdd));
		}

		@Override
		public Set<String> removeAll(Collection<String> keys) {
			Set<String> set = new HashSet<String>();
			for (Iterator<String> i = keys.iterator(); i.hasNext();) {
				String string = i.next();
				if(remove(string))
					set.add(string);
			}
			return set;
		}

		@Override
		public Set<String> removeAll(Collection<String> keys, long millisNoReAdd) {
			Set<String> set = new HashSet<String>();
			for (Iterator<String> i = keys.iterator(); i.hasNext();) {
				String string = i.next();
				if(remove(string,millisNoReAdd))
					set.add(string);
			}
			return set;
		}

		@Override
		public Object get(String key) {
			log.debug("Memcached GET (collection:{} key:{})", collection, key);
			return client.get(collection + key);
		}

		@Override
		public Map<String, Object> getAll(Collection<String> keys) {
			log.debug("Memcached GETMULTI (collection:{} key:{})", collection, keys);
			String[] keysori = keys.toArray(new String[keys.size()]);
			String[] keysary = new String[keys.size()];
			for (int i = 0; i < keysary.length; i++) {
				keysary[i] = collection + keysori[i];
			}
			return client.getMulti(keysary);
		}

		@Override
		public long increment(String key, long delta) {
			log.debug("Memcached INCREMENT (collection:{} key:{})", collection, key);
			return client.incr(collection + key, delta);
		}

		@Override
		public void put(String key, Object value) {
			log.debug("Memcached SET (collection:{} key:{})", collection, key);
			client.set(collection + key, value);
		}

		@Override
		public void put(String key, Object value, int ttlmillis) {
			log.debug("Memcached SET (collection:{} key:{})", collection, key);
			client.set(collection + key, value, new Date(System.currentTimeMillis()+ttlmillis));
		}

		@Override
		public void put(String key, Object value, Date expire) {
			log.debug("Memcached SET (collection:{} key:{})", collection, key);
			client.set(collection + key, value, expire);
		}

		@Override
		public void put(String key, Object value, int ttlmillis, SetPolicy policy) {
			put(key, value, new Date(System.currentTimeMillis()+ttlmillis), policy);
		}

		@Override
		public void put(String key, Object value, Date expire, SetPolicy policy) {
			if(policy == SetPolicy.SET_ALWAYS){
				client.set(collection + key, value, expire);
			}else if(policy == SetPolicy.REPLACE_ONLY_IF_PRESENT){
				client.replace(collection + key, value, expire);
			}else if(policy == SetPolicy.ADD_ONLY_IF_NOT_PRESENT){
				if(!client.keyExists(collection + key))
					client.set(collection + key, value, expire);
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

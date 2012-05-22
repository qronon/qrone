package org.qrone.memcached;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.qrone.kvs.KeyValueStore;

public interface MemcachedService {
	public Memcached getKeyValueStore(String collection);
}

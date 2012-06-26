package org.qrone.r7;

import org.qrone.database.DatabaseService;
import org.qrone.kvs.KeyValueStore;
import org.qrone.kvs.KeyValueStoreService;
import org.qrone.login.LoginService;
import org.qrone.memcached.MemcachedService;
import org.qrone.r7.fetcher.HTTPFetcher;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.resolver.URIFileSystem;
import org.qrone.util.Token;

public class PortingService {
	public HTTPFetcher getURLFetcher() {
		return uriFetcher;
	}
	public void setURLFetcher(HTTPFetcher uriFetcher) {
		this.uriFetcher = uriFetcher;
	}
	public URIResolver getURIResolver() {
		return uriResolver;
	}
	public void setURIResolver(URIResolver uriResolver) {
		this.uriResolver = uriResolver;
	}
	public DatabaseService getDatabaseService() {
		return databaseService;
	}
	public void setDatabaseService(DatabaseService databaseService) {
		this.databaseService = databaseService;
	}
	public MemcachedService getMemcachedService() {
		return memcachedService;
	}
	public void setMemcachedService(MemcachedService memcachedService) {
		this.memcachedService = memcachedService;
	}
	public RepositoryService getRepositoryService() {
		return repositoryService;
	}
	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}
	public KeyValueStoreService getKeyValueStoreService() {
		return keyValueStoreService;
	}
	public void setKeyValueStoreService(KeyValueStoreService keyValueStoreService) {
		this.keyValueStoreService = keyValueStoreService;
	}
	private HTTPFetcher uriFetcher;
	private URIResolver uriResolver;
	private DatabaseService databaseService;
	private MemcachedService memcachedService;
	private RepositoryService repositoryService;
	private KeyValueStoreService keyValueStoreService;
	private URIFileSystem fileSystemService;
	public URIFileSystem getFileSystemService() {
		return fileSystemService;
	}
	public void setFileSystemService(URIFileSystem fileSystemService) {
		this.fileSystemService = fileSystemService;
	}
	
	private Token key;
	public Token getMasterToken(){
		if(key == null){
			KeyValueStore kvs = keyValueStoreService.getKeyValueStore("qrone.setting");
			byte[] keybytes = (byte[])kvs.get("secretkey");
			if(keybytes == null){
				key = new Token(key, "M",null);
				kvs.set("secretkey", key.getBytes());
			}else{
				key = new Token();
			}
		}
		
		return key;
	}
	
}

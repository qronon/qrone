package org.qrone.r7;

import org.qrone.img.ImageBufferService;
import org.qrone.kvs.KVSService;
import org.qrone.memcached.MemcachedService;
import org.qrone.r7.fetcher.URLFetcher;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.script.browser.LoginService;

public class PortingServiceBase  implements PortingService{
	private ImageBufferService imagebuffer;
	private LoginService login;
	private URLFetcher fetcher;
	private URIResolver resolver;
	private KVSService database;
	private MemcachedService memcached;
	private RepositoryService repository;
	
	public PortingServiceBase(
			URLFetcher fetcher,
			URIResolver resolver,
			KVSService database,
			MemcachedService memcached,
			LoginService login,
			ImageBufferService imagebuffer,
			RepositoryService repository
			){
		this.imagebuffer = imagebuffer;
		this.login = login;
		this.fetcher = fetcher;
		this.resolver = resolver;
		this.database = database;
		this.memcached = memcached;
		this.repository = repository;
	}

	@Override
	public ImageBufferService getImageBufferService() {
		return imagebuffer;
	}

	@Override
	public LoginService getLoginService() {
		return login;
	}

	@Override
	public URLFetcher getURLFetcher() {
		return fetcher;
	}

	@Override
	public URIResolver getURIResolver() {
		return resolver;
	}

	@Override
	public KVSService getKVSService() {
		return database;
	}

	@Override
	public MemcachedService getMemcachedService() {
		return memcached;
	}

	@Override
	public RepositoryService getRepositoryService() {
		return repository;
	}

}

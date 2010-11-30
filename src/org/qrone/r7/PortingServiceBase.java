package org.qrone.r7;

import org.qrone.database.DatabaseService;
import org.qrone.img.ImageBufferService;
import org.qrone.login.LoginService;
import org.qrone.login.SecurityService;
import org.qrone.memcached.MemcachedService;
import org.qrone.r7.fetcher.URLFetcher;
import org.qrone.r7.resolver.URIResolver;

public class PortingServiceBase  implements PortingService{
	private ImageBufferService imagebuffer;
	private LoginService login;
	private URLFetcher fetcher;
	private URIResolver resolver;
	private DatabaseService database;
	private MemcachedService memcached;
	private RepositoryService repository;
	private SecurityService security;
	
	public PortingServiceBase(
			URLFetcher fetcher,
			URIResolver resolver,
			DatabaseService database,
			MemcachedService memcached,
			LoginService login,
			ImageBufferService imagebuffer,
			RepositoryService repository,
			SecurityService security
			){
		this.imagebuffer = imagebuffer;
		this.login = login;
		this.fetcher = fetcher;
		this.resolver = resolver;
		this.database = database;
		this.memcached = memcached;
		this.repository = repository;
		this.security = security;
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
	public DatabaseService getKVSService() {
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

	@Override
	public SecurityService getSecurityService() {
		return security;
	}

}

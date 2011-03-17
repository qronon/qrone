package org.qrone.r7;

import org.qrone.database.DatabaseService;
import org.qrone.img.ImageBufferService;
import org.qrone.img.ImageSpriteService;
import org.qrone.login.LoginService;
import org.qrone.login.SecurityService;
import org.qrone.memcached.MemcachedService;
import org.qrone.r7.fetcher.HTTPFetcher;
import org.qrone.r7.resolver.URIResolver;

public interface PortingService {
	public ImageBufferService getImageBufferService();
	public ImageSpriteService getImageSpriteService();
	public LoginService getLoginService();
	public HTTPFetcher getURLFetcher();
	public URIResolver getURIResolver();
	public DatabaseService getKVSService();
	public MemcachedService getMemcachedService();
	public RepositoryService getRepositoryService();
	public SecurityService getSecurityService();
	public TaskManagerService getTaskManagerService();
}

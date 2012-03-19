package org.qrone.r7;

import org.qrone.database.DatabaseService;
import org.qrone.img.ImageBufferService;
import org.qrone.img.ImageSpriteService;
import org.qrone.kvs.KeyValueStoreService;
import org.qrone.login.LoginService;
import org.qrone.login.SecurityService;
import org.qrone.memcached.MemcachedService;
import org.qrone.r7.fetcher.HTTPFetcher;
import org.qrone.r7.resolver.URIResolver;

public class PortingService {
	public ImageBufferService getImageBufferService() {
		return imageBufferService;
	}
	public void setImageBufferService(ImageBufferService imageBufferService) {
		this.imageBufferService = imageBufferService;
	}
	public ImageSpriteService getImageSpriteService() {
		return imageSpriteService;
	}
	public void setImageSpriteService(ImageSpriteService imageSpriteService) {
		this.imageSpriteService = imageSpriteService;
	}
	public LoginService getLoginService() {
		return loginService;
	}
	public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
	}
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
	public SecurityService getSecurityService() {
		return securityService;
	}
	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}
	public TaskManagerService getTaskManagerService() {
		return taskManagerService;
	}
	public void setTaskManagerService(TaskManagerService taskManagerService) {
		this.taskManagerService = taskManagerService;
	}
	public KeyValueStoreService getKeyValueStoreService() {
		return keyValueStoreService;
	}
	public void setKeyValueStoreService(KeyValueStoreService keyValueStoreService) {
		this.keyValueStoreService = keyValueStoreService;
	}
	private ImageBufferService imageBufferService;
	private ImageSpriteService imageSpriteService;
	private LoginService loginService;
	private HTTPFetcher uriFetcher;
	private URIResolver uriResolver;
	private DatabaseService databaseService;
	private MemcachedService memcachedService;
	private RepositoryService repositoryService;
	private SecurityService securityService;
	private TaskManagerService taskManagerService;
	private KeyValueStoreService keyValueStoreService;
	private URIResolver fileSystemService;
	public URIResolver getFileSystemService() {
		return fileSystemService;
	}
	public void setFileSystemService(URIResolver fileSystemService) {
		this.fileSystemService = fileSystemService;
	}
	
}

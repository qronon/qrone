package org.qrone.r7.app;

import javax.servlet.ServletContext;

import org.qrone.database.DatabaseService;
import org.qrone.img.ImageSpriteService;
import org.qrone.kvs.KeyValueStoreService;
import org.qrone.login.CookieHandler;
import org.qrone.png.PNGMemoryImageService;
import org.qrone.r7.PortingService;
import org.qrone.r7.fetcher.HTTPFetcher;
import org.qrone.r7.github.GitHubRepositoryService;
import org.qrone.r7.github.GitHubResolver;
import org.qrone.r7.handler.DefaultHandler;
import org.qrone.r7.handler.ExtendableURIHandler;
import org.qrone.r7.resolver.FilteredResolver;
import org.qrone.r7.resolver.InternalResourceResolver;
import org.qrone.r7.resolver.URIResolver;

public class QrONEURIHandler extends ExtendableURIHandler {
	private PortingService service;
	private GitHubResolver github;
	private GitHubRepositoryService repository;
	
	public QrONEURIHandler( ServletContext cx, PortingService service ){
		this.service = service;
		KeyValueStoreService kvs = service.getKeyValueStoreService();
		HTTPFetcher fetcher = service.getURLFetcher();
		URIResolver cache  = service.getFileSystemService();
		DatabaseService db = service.getDatabaseService();
		service.setURIResolver(resolver);

		// Login/Crumb Service
		CookieHandler cookie = new CookieHandler(kvs);
		
		service.setSecurityService(new CookieHandler(service.getKeyValueStoreService()));
		handler.add(cookie);
		
		// Scale9 Service
		PNGMemoryImageService imagebufferservice = new PNGMemoryImageService();
		ImageSpriteService imagespriteservice = new ImageSpriteService(resolver, cache, imagebufferservice);
		
		service.setImageBufferService(imagebufferservice);
		service.setImageSpriteService(imagespriteservice);
		resolver.add(imagespriteservice);
		
		// Github Admintool
		github = new GitHubResolver(fetcher, cache, 
				"qronon","qrone-admintool","master");
		repository = new GitHubRepositoryService(fetcher, cache, db);
		service.setRepositoryService(repository);
		
		resolver.add(github);
		resolver.add(repository.getResolver());
		resolver.add(new FilteredResolver("/system/resource/", new InternalResourceResolver(cx)));
		resolver.add(cache);
		
		
		handler.add(github);
		handler.add(repository);
		
		// DefaultHandler
		DefaultHandler defaulthandler = new DefaultHandler(service);
		rawextend(defaulthandler);
		rawextend(this);
		handler.add(defaulthandler);
	}
}

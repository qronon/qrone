package org.qrone.r7.script.browser;

import org.qrone.img.ImageBufferService;
import org.qrone.kvs.KVSService;
import org.qrone.memcached.MemcachedService;
import org.qrone.r7.fetcher.URLFetcher;
import org.qrone.r7.resolver.URIResolver;

public interface PortingService {
	public ImageBufferService getImageBufferService();
	public LoginService getLoginService();
	public URLFetcher getURLFetcher();
	public URIResolver getURIResolver();
	public KVSService getKVSService();
	public MemcachedService getMemcachedService();
}

package org.qrone.r7.resolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;

import org.ho.yaml.Yaml;
import org.qrone.r7.fetcher.URLFetcher;
import org.qrone.util.QrONEUtils;

public class GitHubResolver implements URIResolver {
	private URLFetcher fetcher;
	private String user;
	private String repo;
	private String treesha;
	
	private Map<String,String> blobs;
	
	public GitHubResolver(URLFetcher fetcher, URIResolver cacheresolver, String user, String repo, String treesha){
		this.fetcher = fetcher;
		this.user = QrONEUtils.escape(user);
		this.treesha = QrONEUtils.escape(treesha);
	}
	
	public void clear(){
		blobs = null;
	}
	
	private Map<String,String> getFiles(){
		if(blobs == null){
			Map map = (Map)Yaml.load(fetcher.fetch("http://github.com/api/v2/yaml/blob/all/" 
					+ user + "/" + repo + "/" + treesha));
			blobs = (Map<String,String>)map.get("blobs");
		}
		return blobs;
	}
	
	@Override
	public boolean exist(String path) {
		return blobs.containsKey(path);
	}

	@Override
	public boolean updated(URI uri) {
		return false;
	}

	@Override
	public InputStream getInputStream(URI uri) throws IOException {
		String sha = getFiles().get(uri.toString());
		return fetcher.fetch("http://github.com/api/v2/yaml/blob/show/" 
				+ user + "/" + repo + "/" + blobs.get(sha));
	}

	@Override
	public OutputStream getOutputStream(URI uri) throws IOException {
		return null;
	}
}

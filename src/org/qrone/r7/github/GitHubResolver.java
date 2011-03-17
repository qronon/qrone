package org.qrone.r7.github;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

import org.ho.yaml.Yaml;
import org.qrone.r7.fetcher.HTTPFetcher;
import org.qrone.r7.handler.URIHandler;
import org.qrone.r7.resolver.SHAResolver;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.util.QrONEUtils;

public class GitHubResolver implements URIResolver, URIHandler {
	private static final Logger log = Logger.getLogger(GitHubResolver.class.getName());
	private HTTPFetcher fetcher;
	private SHAResolver cacheresolver;
	private String user;
	private String repo;
	private String treesha;
	
	private Map<String,String> blobs;
	private Set<String> updatedSet = new HashSet<String>();
	
	public GitHubResolver(HTTPFetcher fetcher, SHAResolver cacheresolver, String user, String repo, String treesha){
		this.fetcher = fetcher;
		this.cacheresolver = cacheresolver;
		this.user = QrONEUtils.escape(user);
		this.repo = QrONEUtils.escape(repo);
		this.treesha = QrONEUtils.escape(treesha);
	}
	
	public void clear(){
		blobs = null;
	}
	
	private Map<String,String> getFiles(){
		if(blobs == null){
			try {
				Map map = (Map)Yaml.load(fetcher.fetch("http://github.com/api/v2/yaml/blob/all/" 
						+ user + "/" + repo + "/" + treesha));
				blobs = (Map<String,String>)map.get("blobs");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return blobs;
	}
	
	public boolean exist() {
		return getFiles() != null;
	}
	
	@Override
	public boolean exist(String path) {
		getFiles();
		if(cacheresolver.exist(path)) return true;
		if(blobs != null)
			return blobs.containsKey("htdocs" + path);
		return false;
	}

	@Override
	public boolean updated(URI uri) {
		Map<String,String> map = getFiles();
		String path = uri.toString();
		String sha = map.get(path.substring(1));
		if(cacheresolver.updated(uri,sha)) return true;
		return updatedSet.contains(path);
	}

	@Override
	public InputStream getInputStream(URI uri) throws IOException {
		Map<String,String> map = getFiles();
		String sha = map.get("htdocs" + uri.toString());
		InputStream in = cacheresolver.getInputStream(uri, sha);
		if(in != null) return in;
		
		updatedSet.remove(uri.toString());
		if(map != null){
			if(sha != null){
				InputStream fin =  fetcher.fetch("http://github.com/api/v2/yaml/blob/show/" 
						+ user + "/" + repo + "/" + sha);
				byte[] bytes = QrONEUtils.read(fin);
				
				OutputStream out = cacheresolver.getOutputStream(uri,sha);
				out.write(bytes);
				out.flush();
				out.close();
				
				return new ByteArrayInputStream(bytes);
			}
		}
		return null;
	}

	@Override
	public OutputStream getOutputStream(URI uri) throws IOException {
		return null;
	}

	@Override
	public boolean handle(HttpServletRequest request,
			HttpServletResponse response, String uri, String path, String pathArg) {
		if(path.equals("/system/github-post-receive")){
			try {
				log.config("Github post-receive hooks.");
				blobs = null;
				Map map = (Map)JSON.decode(request.getParameter("payload"));
				Map repository = (Map)map.get("repository");
				if(repository.get("name").equals(repo) 
						&& ((Map)repository.get("owner")).get("name").equals(user)){
				
					List<Map> list = (List)map.get("commits");
					
					for (Map m : list) {
						List<String> removed = (List)m.get("removed");
						for (String p : removed) {
							try {
								log.config("removed: /" + p);
								cacheresolver.remove(new URI("/" + p));
								updatedSet.add("/" + p);
							} catch (URISyntaxException e) {}
						}
						List<String> modified = (List)m.get("modified");
						for (String p : modified) {
							try {
								log.config("modified: /" + p);
								cacheresolver.remove(new URI("/" + p));
								updatedSet.add("/" + p);
							} catch (URISyntaxException e) {}
						}
					}
					return true;
				}
				
			} catch (ClassCastException e) {
			}
		}
		return false;
	}
	
	@Override
	public boolean remove(URI uri) {
		return false;
	}

	public void reset() {
		blobs = null;
	}
}

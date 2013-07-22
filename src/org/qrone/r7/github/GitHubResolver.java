package org.qrone.r7.github;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

import org.apache.commons.codec.binary.Base64;
import org.ho.yaml.Yaml;
import org.qrone.r7.fetcher.HTTPFetcher;
import org.qrone.r7.handler.URIHandler;
import org.qrone.r7.resolver.AbstractURIResolver;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.util.QrONEUtils;
import org.qrone.util.Stream;

public class GitHubResolver extends AbstractURIResolver implements URIHandler {
	private static final Logger log = Logger.getLogger(GitHubResolver.class.getName());
	private HTTPFetcher fetcher;
	private URIResolver cacheresolver;
	private String user;
	private String repo;
	private String treesha;
	
	private Map<String,String> blobs;
	private Map<String,String> trees;
	
	public GitHubResolver(HTTPFetcher fetcher, URIResolver cacheresolver, String user, String repo, String treesha){
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
				Map map = (Map)JSON.decode(fetcher.fetch("https://api.github.com/repos/" 
						+ user + "/" + repo + "/git/trees/" + treesha + "?recursive=1"));
				List<Map> list = (List<Map>)map.get("tree");
				
				blobs = new HashMap<String, String>();
				trees = new HashMap<String, String>();
				for (Map o : list) {
					if(o.get("type").equals("blob")){
						blobs.put(o.get("path").toString(), o.get("url").toString());
					}else{
						trees.put(o.get("path").toString(), o.get("url").toString());
					}
					
				}
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
	public boolean existPath(String path) {
		getFiles();
		if(trees != null)
			return trees.containsKey("htdocs" + path);
		return false;
	}

	@Override
	public InputStream getInputStream(URI uri) throws IOException {
		Map<String,String> map = getFiles();
		String url = map.get("htdocs" + uri.toString());
		InputStream in = cacheresolver.getInputStream(uri);
		if(in != null) return in;
		
		if(map != null){
			if(url != null){
				InputStream fin =  fetcher.fetch(url);
				Map m = JSON.decode(fin);
				String encoding = m.get("encoding").toString();
				byte[] bytes = null;
				if(encoding.equals("base64")){
					bytes = Base64.decodeBase64(m.get("content").toString());
				}else{
					bytes = m.get("content").toString().getBytes();
				}
				
				OutputStream out = cacheresolver.getOutputStream(uri);
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
			HttpServletResponse response, String uri, String path, String pathArg, List<String> arg) {
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
								
								URI u = new URI("/" + p);
								cacheresolver.remove(u);
								fireUpdate(u);
							} catch (URISyntaxException e) {}
						}
						List<String> modified = (List)m.get("modified");
						for (String p : modified) {
							try {
								log.config("modified: /" + p);
								
								URI u = new URI("/" + p);
								cacheresolver.remove(u);
								fireUpdate(u);
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

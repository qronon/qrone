package org.qrone.r7.github;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ho.yaml.Yaml;
import org.mozilla.javascript.Scriptable;
import org.qrone.database.DatabaseCursor;
import org.qrone.database.DatabaseService;
import org.qrone.database.DatabaseTable;
import org.qrone.r7.RepositoryService;
import org.qrone.r7.fetcher.HTTPFetcher;
import org.qrone.r7.handler.URIHandler;
import org.qrone.r7.resolver.CascadeResolver;
import org.qrone.r7.resolver.SHAResolver;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.script.Scriptables;

public class GitHubRepositoryService implements URIHandler, RepositoryService{
	private Map<String, GitHubResolver> idToResolverMap = new Hashtable<String, GitHubResolver>();
	private CascadeResolver cascade = new CascadeResolver();
	private DatabaseService service;
	private DatabaseTable table;
	
	private static final String KIND = "qrone.repository";
	private static final String ID = "id";
	private static final String OWNER = "owner";
	private static final String NAME = "name";
	private static final String TREE_SHA = "tree_sha";
	
	private HTTPFetcher fetcher;
	private SHAResolver cacher;
	
	public GitHubRepositoryService(HTTPFetcher fetcher, SHAResolver cacher, DatabaseService service){
		this.fetcher = fetcher;
		this.cacher = cacher;
		this.service = service;
		this.table = service.getCollection(KIND);
		
		DatabaseCursor cursor = table.find();
		while(cursor.hasNext()){
			addGithub(cursor.next());
		}
	}
	
	private GitHubResolver addGithub(Map e){
		GitHubResolver github = new GitHubResolver(fetcher, cacher, 
				(String)e.get(OWNER), (String)e.get(NAME), (String)e.get(TREE_SHA));
		if(github.exist()){
			cascade.add(github);
			return github;
		}
		return null;
	}
	
	public URIResolver getResolver(){
		return cascade;
	}
	
	@Override
	public String add(Scriptable s) {
		Map repo = Scriptables.asMap(s);
		Map map = new Hashtable();
		map.put(OWNER, repo.get(OWNER).toString());
		map.put(NAME, repo.get(NAME).toString());
		map.put(TREE_SHA, repo.get(TREE_SHA).toString());
		
		GitHubResolver resolver = addGithub(map);
		if(resolver != null){
			String id = table.insert(map);
			idToResolverMap.put(id, resolver);
			return id;
		}
		
		return null;
	}

	@Override
	public void remove(String id) {
		table.remove(id);
		
		GitHubResolver resolver = idToResolverMap.get(id);
		if(resolver != null){
			idToResolverMap.remove(id);
			cascade.asList().remove(resolver);
		}
	}

	@Override
	public List<Map<String, Object>> list() {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();

		DatabaseCursor cursor = table.find();
		while(cursor.hasNext()){
			list.add(cursor.next());
		}
		
		return list;
	}

	@Override
	public boolean handle(HttpServletRequest request,
			HttpServletResponse response, String uri, String requestPath,
			String requestPathArg) {
		List l = cascade.asList();
		for (Object o : l) {
			
			((URIHandler)o).handle(request, response, uri, requestPath, requestPathArg);
		}
		return false;
	}

	@Override
	public void reset(String id) {
		GitHubResolver resolver = idToResolverMap.get(id);
		if(resolver != null){
			resolver.reset();
		}
	}
	
	public Map<String, InputStream> getFiles(URI uri){
		Map<String, InputStream> map = new Hashtable<String, InputStream>();
		for (Iterator<Entry<String, GitHubResolver>> iter = idToResolverMap.entrySet().iterator(); iter
				.hasNext();) {
			Entry<String, GitHubResolver> entry = iter.next();
			try {
				map.put(entry.getKey(), entry.getValue().getInputStream(uri));
			} catch (IOException e) {
				map.put(entry.getKey(), null);
			}
		}
		return map;
	}
}

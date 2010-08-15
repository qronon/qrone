package org.qrone.r7.appengine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import org.qrone.r7.QrONEUtils;
import org.qrone.r7.resolver.URIResolver;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class AppEngineResolver implements URIResolver{
	private static final String KIND = "qrone.filesystem";
	private static final String DATE = "date";
	private static final String PATH = "path";
	private static final String DATA = "data";
	
	private DatastoreService service;
	private Map<String, Key> map = new Hashtable<String, Key>();
	private Map<String, Date> lastmap = new Hashtable<String, Date>();
	
	public AppEngineResolver() {
		service = DatastoreServiceFactory.getDatastoreService();
	}
	
	private Key lookup(String path){
		Key key = map.get(path);
		if(key != null){
			return key;
		}else{
			Entity e = get(path);
			if(e != null){
				map.put(path, e.getKey());
				return e.getKey();
			}else{
				return null;
			}
		}
	}
	
	private Entity get(String path){
		lastmap.put(path, QrONEUtils.now());
		
		Query query = new Query(KIND);
		query.addFilter(PATH, FilterOperator.EQUAL, path);
		PreparedQuery pquery = service.prepare(query);
		return pquery.asSingleEntity();
	}

	@Override
	public boolean exist(String path) {
		return lookup(path) != null;
	}

	@Override
	public boolean updated(URI uri) {
		String path = uri.toString();
		Entity e = get(path);
		return lastmap.get(path).compareTo((Date)e.getProperty(DATE)) > 0;
	}

	@Override
	public InputStream getInputStream(URI uri) throws IOException {
		if(lookup(uri.toString()) != null){
			Entity e = get(uri.toString());
			Blob b = (Blob)e.getProperty(DATA);
			return new ByteArrayInputStream(b.getBytes());
		}
		return null;
	}

	@Override
	public OutputStream getOutputStream(URI uri) throws IOException {
		String path = uri.toString();
		return new AppEngineOutputStream(lookup(path), path);
	}
	
	private class AppEngineOutputStream extends ByteArrayOutputStream{
		private Key key;
		private String path;
		public AppEngineOutputStream(Key key, String path) {
			this.key = key;
			this.path = path;
		}
		
		@Override
		public void close() throws IOException {
			super.close();
			Entity e  =null;
			if(key != null)
				e = new Entity(key);
			else
				e = new Entity(KIND);
			
			e.setProperty(DATE, QrONEUtils.now());
			e.setProperty(PATH, path);
			e.setProperty(DATA, new Blob(toByteArray()));
			service.put(e);
		}
	}

}

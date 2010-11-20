package org.qrone.r7.resolver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class MemoryResolver implements URIResolver{
	
	private Map<URI, MemoryOutputStream> map = new Hashtable<URI, MemoryOutputStream>();
	private Map<URI, Long> lastModifiedMap = new Hashtable<URI, Long>();
	private Set<String> existsMap = new HashSet<String>();
	
	public MemoryResolver() {
	}

	@Override
	public boolean exist(String uri) {
		return existsMap.contains(uri);
	}

	@Override
	public InputStream getInputStream(URI uri) throws IOException {
		MemoryOutputStream out = map.get(uri);
		if(out != null)
			return out.getInputStream();
		return null;
	}

	@Override
	public OutputStream getOutputStream(URI uri) throws IOException {
		MemoryOutputStream out = new MemoryOutputStream();
		map.put(uri, out);
		existsMap.add(uri.getPath());
		return out;
	}

	@Override
	public boolean updated(URI uri) {
		Long l = lastModifiedMap.get(uri);
		if(l == null){
			return false;
		}
		return l > System.currentTimeMillis();
	}
	
	private static class MemoryOutputStream extends ByteArrayOutputStream{
		public InputStream getInputStream(){
			return new ByteArrayInputStream(buf);
		}
	}

	@Override
	public boolean remove(URI uri) {
		map.remove(uri);
		lastModifiedMap.remove(uri);
		existsMap.remove(uri.toString());
		return true;
	}
}

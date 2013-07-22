package org.qrone.r7.resolver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Hashtable;
import java.util.Map;

public class MemoryResolver extends AbstractURIResolver{
	
	private Map<URI, MemoryOutputStream> map = new Hashtable<URI, MemoryOutputStream>();
	
	public MemoryResolver() {
	}

	@Override
	public boolean exist(String uri) {
		return map.containsKey(uri);
	}

	@Override
	public boolean existPath(String uri) {
		return false;
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
		return out;
	}
	
	private static class MemoryOutputStream extends ByteArrayOutputStream{
		public InputStream getInputStream(){
			return new ByteArrayInputStream(buf);
		}
	}

	@Override
	public boolean remove(URI uri) {
		map.remove(uri);
		return true;
	}
}

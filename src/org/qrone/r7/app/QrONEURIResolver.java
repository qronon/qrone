package org.qrone.r7.app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Hashtable;
import java.util.Map;

import org.qrone.r7.QrONEUtils;
import org.qrone.r7.resolver.URIResolver;

public class QrONEURIResolver implements URIResolver{

	private File root;
	private Map<URI, ByteArrayOutputStream> map = new Hashtable<URI, ByteArrayOutputStream>();
	private Map<URI, Long> lastModifiedMap = new Hashtable<URI, Long>();
	
	public QrONEURIResolver(File basedir) {
		root = basedir;
	}

	@Override
	public boolean exist(URI uri) {
		return new File(root, uri.toString()).exists();
	}

	@Override
	public InputStream getInputStream(URI uri) throws IOException {
		if(uri.toString().startsWith("qrone-server/")){
			return QrONEUtils.getResourceAsStream(uri.toString().substring("qrone-server/".length()));
		}else{
			File f = new File(root, uri.toString());
			lastModifiedMap.put(uri, f.lastModified());
			return new FileInputStream(f);
		}
	}

	@Override
	public OutputStream getOutputStream(URI uri) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		map.put(uri, out);
		return out;
	}
	
	public boolean hasBuffer(URI uri){
		return map.containsKey(uri);
	}
	
	public void writeTo(URI uri, OutputStream out) throws IOException{
		map.get(uri).writeTo(out);
	}

	@Override
	public boolean updated(URI uri) {
		File f = new File(root, uri.toString());
		Long l = lastModifiedMap.get(uri);
		if(l == null){
			return false;
		}
		return l > f.lastModified();
	}

}

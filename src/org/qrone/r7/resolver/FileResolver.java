package org.qrone.r7.resolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Hashtable;
import java.util.Map;

public class FileResolver implements URIResolver {
	private File root;
	private Map<String, Long> lastModifiedMap = new Hashtable<String, Long>();
	
	public FileResolver(File basedir) {
		root = basedir;
	}

	@Override
	public boolean exist(String uri) {
		return new File(root, uri).exists();
	}

	@Override
	public InputStream getInputStream(URI uri) throws FileNotFoundException {
		File f = new File(root, uri.getPath());
		lastModifiedMap.put(uri.getPath(), f.lastModified());
		return new FileInputStream(f);
	}

	@Override
	public boolean updated(URI uri) {
		File f = new File(root, uri.getPath());
		Long l = lastModifiedMap.get(uri.getPath());
		if(l == null){
			return false;
		}
		return l < f.lastModified();
	}
	@Override
	public OutputStream getOutputStream(URI uri) throws FileNotFoundException {
		return new FileOutputStream(new File(root, uri.getPath()));
	}

}

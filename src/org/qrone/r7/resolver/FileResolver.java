package org.qrone.r7.resolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.qrone.util.UnicodeInputStream;

public class FileResolver extends AbstractURIResolver{
	private File root;
	private boolean noOutput = false;
	private Map<String, Long> map = new HashMap<String, Long>();

	public FileResolver(File basedir) {
		root = basedir;
	}
	
	public FileResolver(File basedir, boolean noOutput) {
		root = basedir;
		this.noOutput = noOutput;
	}

	@Override
	public boolean exist(String uri) {
		File f = new File(root, uri.substring(1));
		boolean r = f.isFile();
		if(r){
			long curr = f.lastModified();
			Long last = map.get(uri);
			if(last != null && curr > last.longValue()){
				try {
					fireUpdate(new URI(uri));
				} catch (URISyntaxException e) {}
			}
			
			map.put(uri, f.lastModified());			
		}
		return r;
	}

	@Override
	public boolean existPath(String uri) {
		File f = new File(root, uri.substring(1));
		return f.isDirectory();
	}

	@Override
	public InputStream getInputStream(URI uri) throws FileNotFoundException {
		File f = new File(root, uri.getPath().substring(1));
		if(f.exists())
			return new UnicodeInputStream(new FileInputStream(f));
		else
			return null;
	}

	@Override
	public OutputStream getOutputStream(URI uri) throws FileNotFoundException {
		if(noOutput) return null;
		File f = new File(root, uri.getPath().substring(1));
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				return null;
			}
		}
		
		fireUpdate(uri);
		return new FileOutputStream(f);
	}

	@Override
	public boolean remove(URI uri) {
		if(noOutput) return false;
		File f = new File(root, uri.getPath());
		if(f.exists())
			return f.delete();
		else
			return false;
	}
	
}

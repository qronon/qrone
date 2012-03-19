package org.qrone.r7.resolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.qrone.util.UnicodeInputStream;

public class FileResolver extends AbstractURIResolver{
	private File root;
	
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
		return new UnicodeInputStream(new FileInputStream(f));
	}

	@Override
	public OutputStream getOutputStream(URI uri) throws FileNotFoundException {
		fireUpdate(uri);
		return new FileOutputStream(new File(root, uri.getPath()));
	}

	@Override
	public boolean remove(URI uri) {
		return new File(root, uri.getPath()).delete();
	}
	
}

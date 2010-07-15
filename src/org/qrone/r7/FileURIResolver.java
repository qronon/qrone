package org.qrone.r7;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.qrone.r7.parser.URIResolver;

public class FileURIResolver implements URIResolver {
	private File root;
	public FileURIResolver(File basedir) {
		root = basedir;
	}

	@Override
	public boolean exist(URI uri) {
		return new File(root, uri.toString()).exists();
	}

	@Override
	public InputStream getInputStream(URI uri) throws FileNotFoundException {
		return new FileInputStream(new File(root, uri.toString()));
	}

	@Override
	public OutputStream getOutputStream(URI uri) throws FileNotFoundException {
		return new FileOutputStream(new File(root, uri.toString()));
	}

}

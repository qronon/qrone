package org.qrone.r7.parser;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public interface URIResolver {
	public boolean exist( URI uri );
	public InputStream getInputStream( URI uri ) throws FileNotFoundException;
	public OutputStream getOutputStream(URI resolve) throws FileNotFoundException;
}

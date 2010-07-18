package org.qrone.r7.resolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public interface URIResolver {
	public boolean exist( URI uri );
	public boolean updated( URI uri );
	public InputStream getInputStream( URI uri ) throws IOException;
	public OutputStream getOutputStream(URI resolve) throws IOException;
}

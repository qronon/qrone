package org.qrone.r7.resolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public interface SHAResolver extends URIResolver{
	public boolean updated( URI uri, String sha );
	public InputStream getInputStream( URI uri, String sha ) throws IOException;
	public OutputStream getOutputStream( URI uri, String sha ) throws IOException;
}

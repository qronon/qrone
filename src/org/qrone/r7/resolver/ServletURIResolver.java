package org.qrone.r7.resolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;

import javax.servlet.ServletContext;



public class ServletURIResolver implements URIResolver{

	private ServletContext context;
	public ServletURIResolver(ServletContext context) {
		this.context = context;
	}
	
	@Override
	public boolean exist(URI uri) {
		try {
			return context.getResource(uri.toString()) != null;
		} catch (MalformedURLException e) {
			return false;
		}
	}

	@Override
	public InputStream getInputStream(URI uri) throws IOException {
		return context.getResourceAsStream(uri.toString());
	}

	@Override
	public OutputStream getOutputStream(URI resolve) throws IOException {
		throw new IOException("Can't output to servletcontext.");
	}

	@Override
	public boolean updated(URI uri) {
		return false;
	}

}

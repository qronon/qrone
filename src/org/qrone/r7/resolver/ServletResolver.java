package org.qrone.r7.resolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;

import javax.servlet.ServletContext;

public class ServletResolver implements URIResolver{

	private ServletContext context;
	public ServletResolver(ServletContext context) {
		this.context = context;
	}
	
	@Override
	public boolean exist(String uri) {
		try {
			return context.getResource(uri) != null;
		} catch (MalformedURLException e) {
			return false;
		}
	}

	@Override
	public InputStream getInputStream(URI uri) throws IOException {
		return context.getResourceAsStream(uri.getPath());
	}
	
	@Override
	public boolean updated(URI uri) {
		return false;
	}
	@Override
	public OutputStream getOutputStream(URI uri) throws IOException {
		return null;
	}
}

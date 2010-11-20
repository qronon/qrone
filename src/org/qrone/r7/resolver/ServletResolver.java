package org.qrone.r7.resolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import javax.servlet.ServletContext;

import org.qrone.util.UnicodeInputStream;

public class ServletResolver implements URIResolver{

	private ServletContext context;
	public ServletResolver(ServletContext context) {
		this.context = context;
	}
	
	@Override
	public boolean exist(String uri) {
		try {
			URL url = context.getResource(uri);
			if (url == null) {
				uri = "/WEB-INF" + uri;
				url = context.getResource(uri);
			}
			if (url == null) {
				return false;
			} else {
				url = new URL("file", "", context.getRealPath(uri));
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public InputStream getInputStream(URI u) throws IOException {
		try {
			String uri = u.toString();
			URL url = context.getResource(uri);
			if (url == null) {
				uri = "/WEB-INF" + uri;
				url = context.getResource(uri);
			}
			if (url == null) {
				return null;
			} else {
				url = new URL("file", "", context.getRealPath(uri));
			}
			return new UnicodeInputStream(url.openConnection().getInputStream());
		} catch (IOException e) {
			return null;
		}
	}
	
	@Override
	public boolean updated(URI uri) {
		return false;
	}
	@Override
	public OutputStream getOutputStream(URI uri) throws IOException {
		return null;
	}

	@Override
	public boolean remove(URI uri) {
		return false;
	}
}

package org.qrone.r7.resolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.servlet.ServletContext;

import org.qrone.util.QrONEUtils;
import org.qrone.util.UnicodeInputStream;

public class InternalResourceResolver implements URIResolver{
	private ServletContext cx;
	public InternalResourceResolver(ServletContext cx) {
		this.cx = cx;
	}
	
	@Override
	public boolean exist(String uri) {
		try {
			return QrONEUtils.getResourceAsStream(uri,cx) != null;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public InputStream getInputStream(URI uri) throws IOException {
		return new UnicodeInputStream(QrONEUtils.getResourceAsStream(uri.toString(),cx));
	}
	
	@Override
	public OutputStream getOutputStream(URI uri) throws IOException {
		return null;
	}

	@Override
	public boolean remove(URI uri) {
		return false;
	}

	@Override
	public void addUpdateListener(Listener l) {
	}
}

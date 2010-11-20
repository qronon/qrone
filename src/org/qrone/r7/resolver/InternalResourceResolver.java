package org.qrone.r7.resolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.qrone.util.QrONEUtils;
import org.qrone.util.UnicodeInputStream;

public class InternalResourceResolver implements URIResolver{
	
	@Override
	public boolean exist(String uri) {
		try {
			return QrONEUtils.getResourceAsStream(uri) != null;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public InputStream getInputStream(URI uri) throws IOException {
		return new UnicodeInputStream(QrONEUtils.getResourceAsStream(uri.toString()));
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

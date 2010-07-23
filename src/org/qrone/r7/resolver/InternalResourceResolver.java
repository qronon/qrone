package org.qrone.r7.resolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.qrone.r7.QrONEUtils;

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
		return QrONEUtils.getResourceAsStream(uri.toString());
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

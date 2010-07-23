package org.qrone.r7.resolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class FilteredResolver implements URIResolver{
	private URIResolver r;
	private String prefix;
	public FilteredResolver(String prefix, URIResolver r) {
		this.prefix = prefix;
		this.r = r;
	}
	
	private URI prefixed(URI uri){
		String u = uri.toString();
		if(u.startsWith(prefix)){
			try {
				return new URI(u.substring(prefix.length()));
			} catch (URISyntaxException e) {
			}
		}
		return null;
	}
	
	@Override
	public boolean exist(String uri) {
		if(uri.startsWith(prefix))
			return r.exist(uri.substring(prefix.length()));
		return false;
	}

	@Override
	public InputStream getInputStream(URI uri) throws IOException {
		uri = prefixed(uri);
		if(uri != null)
			return r.getInputStream(uri);
		return null;
	}

	@Override
	public OutputStream getOutputStream(URI uri) throws IOException {
		uri = prefixed(uri);
		if(uri != null)
			return r.getOutputStream(uri);
		return null;
	}

	@Override
	public boolean updated(URI uri) {
		uri = prefixed(uri);
		if(uri != null)
			return r.updated(uri);
		return false;
	}

}

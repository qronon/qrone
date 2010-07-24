package org.qrone.r7.resolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class CascadeResolver implements URIResolver{
	private List<URIResolver> list = new ArrayList<URIResolver>();
	
	public void add(URIResolver r){
		list.add(r);
	}
	
	public void add(int index, URIResolver r){
		list.add(index, r);
	}

	@Override
	public boolean exist(String uri) {
		for (Iterator<URIResolver> i = list.iterator(); i
				.hasNext();) {
			URIResolver r = i.next();
			if(r.exist(uri)) return true;
		}
		return false;
	}

	@Override
	public InputStream getInputStream(URI uri) throws IOException {
		for (Iterator<URIResolver> i = list.iterator(); i
				.hasNext();) {
			URIResolver r = i.next();
			InputStream in = r.getInputStream(uri);
			if(in != null) return in;
		}
		return null;
	}

	@Override
	public OutputStream getOutputStream(URI uri) throws IOException {
		for (Iterator<URIResolver> i = list.iterator(); i
				.hasNext();) {
			URIResolver r = i.next();
			OutputStream out = r.getOutputStream(uri);
			if(out != null) return out;
		}
		return null;
	}

	@Override
	public boolean updated(URI uri) {
		for (Iterator<URIResolver> i = list.iterator(); i
				.hasNext();) {
			URIResolver r = i.next();
			if(r.updated(uri)) return true;
		}
		return false;
	}
	
}

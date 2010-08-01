package org.qrone.deck;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Hashtable;
import java.util.Map;

import org.qrone.r7.resolver.URIResolver;


public abstract class XDeck<T> {
	private Map<URI, T> map = new Hashtable<URI, T>();
	protected URIResolver resolver;
	
	public XDeck(URIResolver resolver) {
		this.resolver = resolver;
	}

    
    public URIResolver getResolver(){
    	return resolver;
    }
    
    protected boolean updated(T t, URI uri){
    	return resolver.updated(uri);
    }
	
	public T compile(URI uri){
		T t = map.get(uri);
		if(t == null || updated(t, uri)){
			try {
				InputStream in = resolver.getInputStream(uri);
				t = compile(uri, in, "utf8");
				map.put(uri, t);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return t;
	}
	
	protected abstract T compile(URI uri, InputStream in, String encoding) throws Exception;
}

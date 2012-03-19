package org.qrone.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.WeakHashMap;

import org.qrone.r7.resolver.URIResolver;


public abstract class XDeck<T> {
	private Map<URI, T> map = new WeakHashMap<URI, T>();
	protected URIResolver resolver;
	
	public XDeck(URIResolver resolver) {
		this.resolver = resolver;
		resolver.addUpdateListener(new URIResolver.Listener() {
			@Override
			public void update(URI uri) {
				map.remove(uri);
			}
		});
	}

    
    public URIResolver getResolver(){
    	return resolver;
    }
   
    /*
    protected boolean updated(T t, URI uri){
    	return resolver.updated(uri);
    }*/
    
	
	public T compile(URI uri){
		T t = map.get(uri);
		if(t == null){
			InputStream in = null;
			try {
				in = resolver.getInputStream(uri);
				t = compile(uri, in, "utf8");
				map.put(uri, t);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(in != null){
					try{
						in.close();
					}catch(IOException e){}
				}
			}
		}
		return t;
	}
	
	protected abstract T compile(URI uri, InputStream in, String encoding) throws Exception;
}

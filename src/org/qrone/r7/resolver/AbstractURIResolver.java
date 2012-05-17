package org.qrone.r7.resolver;

import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractURIResolver implements URIResolver{
	private Set<Listener> listeners = new HashSet<URIResolver.Listener>();
	
	@Override
	public void addUpdateListener(Listener l) {
		listeners.add(l);
	}
	
	public void fireUpdate(URI uri){
		for (Iterator<Listener> iter = listeners.iterator(); iter.hasNext();) {
			iter.next().update(uri);
		}
	}
	
}

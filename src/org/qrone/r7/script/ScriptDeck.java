package org.qrone.r7.script;

import java.net.URI;

import org.qrone.r7.resolver.URIResolver;

public class ScriptDeck {
	private URIResolver resolver;

    public ScriptDeck(URIResolver resolver){
    	this.resolver = resolver;
    }
    
    public URIResolver getResolver(){
    	return resolver;
    }
    
	public ScriptOM compile(URI uri){
		return new ScriptOM(this, uri);
	}
}

package org.qrone.r7.parser;

import java.io.InputStream;
import java.net.URI;
import java.util.Hashtable;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.qrone.r7.Extendable;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.script.SugarWrapFactory;
import org.qrone.util.XDeck;

public class JSDeck extends XDeck<JSOM> implements Extendable{
	private HTML5Deck deck;
	private Map<Thread, Context> map = new Hashtable<Thread, Context>();
	private Scriptable globalScope;
	private SugarWrapFactory wrapFactory;

    public JSDeck(URIResolver resolver, HTML5Deck deck){
    	super(resolver);
    	this.deck = deck;
    	wrapFactory = new SugarWrapFactory();
    }
    
    public HTML5Deck getHTML5Deck(){
    	return deck;
    }

	@Override
	public JSOM compile(URI uri, InputStream in, String encoding)
			throws Exception {
		JSOM om = new JSOM(this);
		om.parser(uri);
		return om;
	}
	
	public Scriptable createScope(){
		Context cx = getContext();
		Scriptable global = getGlobalScope();
		return cx.newObject(global);
	}
	
	public Scriptable getGlobalScope(){
		if(globalScope == null){
			globalScope = getContext().initStandardObjects();
		}
		return globalScope;
	}
	
	public Context getContext(){
		Thread t = Thread.currentThread();
		Context cx = map.get(t);
		if(cx == null){
			cx = Context.enter();
			cx.setOptimizationLevel(9);
			cx.setWrapFactory(wrapFactory);
			map.put(t, cx);
		}
		return cx;
	}
	
	public void addExtension(Class wrapper) {
		wrapFactory.addExtension(wrapper);
	}
}

package org.qrone.r7.parser;

import java.io.IOException;
import java.net.URI;
import java.util.Hashtable;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.script.Window;

public class JSDeck {
	private URIResolver resolver;
	private HTML5Deck deck;
	private Map<URI, JSOM> ommap = new Hashtable<URI, JSOM>();
	private Map<Thread, Context> map = new Hashtable<Thread, Context>();
	private Scriptable globalScope;
	private Window window;

    public JSDeck(URIResolver resolver, HTML5Deck deck){
    	this.resolver = resolver;
    	this.deck = deck;
    }
    
    public URIResolver getResolver(){
    	return resolver;
    }
    
    public HTML5Deck getHTML5Deck(){
    	return deck;
    }
    
	public JSOM compile(URI uri) throws IOException{
		JSOM om = ommap.get(uri);
		if(om == null){
			om = new JSOM(this);
			om.parser(uri);
		}
		return om;
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
			map.put(t, cx);
		}
		return cx;
	}
	/*
	public static void main(String[] args) {
    	
		long timer = System.currentTimeMillis();
    	
		Context cx = Context.enter();
		cx.setOptimizationLevel(9);
		Scriptable scope = cx.initStandardObjects();

		
		long inittime = System.currentTimeMillis() - timer;

		Script scr = cx.compileString("var test = 'Hello 4';window.out.println(window.test);", "source", 0, null);

    	long evaltime = System.currentTimeMillis() - timer;

        System.err.println("Init time " + inittime + "ms");
        System.err.println("Eval time " + evaltime + "ms");
        
		for (int i = 0; i < 10; i++) {
			
			Scriptable window = (Scriptable)Context.javaToJS(new Window(), scope);
			scope.setPrototype(window);
			scope.put("window", scope, scope);
			scr.exec(cx, scope);
			
	    	long alltime = System.currentTimeMillis() - timer;
	    	
	        System.err.println("All time " + alltime + "ms");

		}
	}
	*/
}

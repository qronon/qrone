package org.qrone.r7.parser;

import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Hashtable;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.script.ScriptableJavaObject;
import org.qrone.r7.script.SugarWrapFactory;
import org.qrone.util.XDeck;

public class JSDeck extends XDeck<JSOM>{
	private HTML5Deck deck;
	private static Map<Thread, Context> map = new Hashtable<Thread, Context>();
	private static Scriptable globalScope;
	private static SugarWrapFactory wrapFactory = new SugarWrapFactory();

    public JSDeck(URIResolver resolver, HTML5Deck deck){
    	super(resolver);
    	this.deck = deck;
    }
    
    public HTML5Deck getHTML5Deck(){
    	return deck;
    }
    
    public static SugarWrapFactory getSugarWrapFactory(){
    	return wrapFactory;
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
	
	public static Scriptable getGlobalScope(){
		if(globalScope == null){
			globalScope = getContext().initStandardObjects();
		}
		return globalScope;
	}
	
	public static Context getContext(){
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

	private static Class getGenericType(Class cls) {
		Type[] types = cls.getGenericInterfaces();
		for (int i = 0; i < types.length; i++) {
			if(types[i] instanceof ParameterizedType){
				ParameterizedType ty = (ParameterizedType) types[i];
				if(ty.getRawType().equals(ScriptableJavaObject.class)){
					Type[] actualType = ty.getActualTypeArguments();
					if (actualType.length > 0 && actualType[0] instanceof Class) {
						return (Class) actualType[0];
					}
				}
			}
		}
		return null;
	}
	
	public void addExtension(Class<? extends ScriptableJavaObject> wrapper) {
		Class cls = getGenericType(wrapper);
		getSugarWrapFactory().addWrapperClass(cls, wrapper);
	}
}

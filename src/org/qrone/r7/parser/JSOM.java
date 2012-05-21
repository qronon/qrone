package org.qrone.r7.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.qrone.r7.PortingService;
import org.qrone.r7.script.browser.Window;
import org.qrone.r7.script.window.WindowPrototype;

public class JSOM implements Comparable<JSOM>{
	private URI uri;
	private JSDeck deck;
	private Script script;

	public JSOM(JSDeck deck) {
		this.deck = deck;
	}
	
	public void parser(URI uri) throws IOException{
		InputStream in = deck.getResolver().getInputStream(uri);
		try{
			this.uri = uri;
			script = deck.getContext().compileReader(new InputStreamReader(
					in, "utf8"), 
					uri.toString(), 0, null);
		}finally{
			in.close();
		}
	}
	
	public Object run(Scriptable scope){
		scope.put("window", scope, scope);
		return script.exec(deck.getContext(), scope);
	}

	public Object run(Scriptable scope, Window win, Object... prototypes){
		Scriptable parent = scope;
		for (int i = 0; i < prototypes.length; i++) {
			Scriptable child = (Scriptable)Context.javaToJS(prototypes[i], scope);
			parent.setPrototype(child);
			parent = child;
		}
		
		Set<Class> pset = deck.getWindowPrototypes();
		for ( Class cls : pset) {
			try {
				WindowPrototype obj = (WindowPrototype)cls
						.getConstructor(Window.class).newInstance(win);
				
				Scriptable child = (Scriptable)Context.javaToJS(obj, scope);
				obj.init(child);
				
				parent.setPrototype(child);
				parent = child;
				
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		return run(scope);
	}

	@Override
	public int compareTo(JSOM o) {
		return uri.compareTo(o.uri);
	}

}

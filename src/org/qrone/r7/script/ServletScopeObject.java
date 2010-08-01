package org.qrone.r7.script;

import java.net.URI;
import java.net.URISyntaxException;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class ServletScopeObject {
	protected ServletScope ss;
	
	public ServletScopeObject(ServletScope ss) {
		this.ss = ss;
	}
	
	protected Scriptable newScriptable(){
		return ss.vm.getContext().newObject(ss.scope);
	}
	
	protected void callJSFunction(Callable func, Object thisObj, Object ... args){
		func.call(ss.vm.getContext(), ss.scope, 
				(Scriptable)Context.javaToJS(thisObj,ss.scope), args);
	}

	protected URI resolvePath(String path) throws URISyntaxException {
		return ss.uri.resolve(new URI(path));
	}
}
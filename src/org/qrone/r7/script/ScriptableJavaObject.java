package org.qrone.r7.script;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.qrone.r7.resolver.URIResolver;

public class ScriptableJavaObject<T>{
	protected Context context;
	protected Scriptable scope;
	protected T parent;
	protected Class staticType;
	protected Scriptable thisObj;

    public ScriptableJavaObject(Context cx, Scriptable scope, T parent, Class staticType, Scriptable thisObj) {
        this.context = cx;
        this.scope = scope;
    	this.parent = parent;
    	this.staticType = staticType;
        this.thisObj = thisObj;
    }
	
	protected void callJSFunction(Callable func, Object ... args){
		func.call(context, scope, thisObj, args);
	}
	
}

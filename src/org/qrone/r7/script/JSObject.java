package org.qrone.r7.script;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class JSObject {
	protected ServletScope ss;
	
	public JSObject(ServletScope ss) {
		this.ss = ss;
	}
	
	protected Scriptable newScriptable(){
		return ss.vm.getContext().newObject(ss.scope);
	}
	
	protected void callJSFunction(Callable func, Object thisObj, Object ... args){
		func.call(ss.vm.getContext(), ss.scope, 
				(Scriptable)Context.javaToJS(thisObj,ss.scope), args);
	}
}
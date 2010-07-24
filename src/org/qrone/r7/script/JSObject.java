package org.qrone.r7.script;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.qrone.r7.parser.JSDeck;

public class JSObject {
	protected ServletScope ss;
	
	public JSObject(ServletScope ss) {
		this.ss = ss;
	}
	
	protected void callJSFunction(Callable func, Object thisObj, Object ... args){
		func.call(ss.vm.getContext(), ss.scope, 
				(Scriptable)Context.javaToJS(thisObj,ss.scope), args);
	}
}
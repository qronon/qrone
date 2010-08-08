package org.qrone.r7.script;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

public class ContextPack<T>{
	public Context context;
	public Scriptable scope;
	public T parent;
	public Class staticType;
	public Scriptable thisObj;

	public ContextPack(Context context, Scriptable scope, T parent,
			Class staticType, NativeJavaObject wrapper) {
		this.context = context;
		this.scope = scope;
		this.parent = parent;
		this.staticType = staticType;
		this.thisObj = wrapper;
	}

	public void call(Callable func, Object ... args){
		func.call(context, scope, thisObj, args);
	}
}

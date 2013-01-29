package org.qrone.r7.script;

import org.mozilla.javascript.Scriptable;

public abstract class AbstractScriptable implements Scriptable{
	private Scriptable scope;
	private Scriptable prototype;
	
	public AbstractScriptable(){
	}

	@Override
	public void delete(String arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(int arg0) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Object get(int arg0, Scriptable arg1) {
		return get(String.valueOf(arg0), arg1);
	}

	@Override
	public String getClassName() {
		return "JSScriptable";
	}

	@Override
	public Object getDefaultValue(Class arg0) {
		return null;
	}

	@Override
	public Scriptable getParentScope() {
		return scope;
	}

	@Override
	public Scriptable getPrototype() {
		return prototype;
	}

	@Override
	public boolean has(String arg0, Scriptable arg1) {
		return get(arg0, arg1) != null;
	}

	@Override
	public boolean has(int arg0, Scriptable arg1) {
		return get(arg0, arg1) != null;
	}

	@Override
	public boolean hasInstance(Scriptable arg0) {
		return false;
	}

	@Override
	public void put(String arg0, Scriptable arg1, Object arg2) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void put(int arg0, Scriptable arg1, Object arg2) {
		put(String.valueOf(arg0), arg1, arg2);
	}

	@Override
	public void setParentScope(Scriptable arg0) {
		scope = arg0;
		
	}

	@Override
	public void setPrototype(Scriptable arg0) {
		prototype = arg0;
	}

}

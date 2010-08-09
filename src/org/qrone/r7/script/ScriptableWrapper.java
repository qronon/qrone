package org.qrone.r7.script;

import java.util.Collection;
import java.util.Set;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Wrapper;


public abstract class ScriptableWrapper<T> extends NativeJavaObject implements Scriptable, Wrapper{
	private static final long serialVersionUID = -2463022914755259679L;

	public ScriptableWrapper() {
	}
	
	public ScriptableWrapper(T javaObject) {
	}
	
	public ScriptableWrapper(Scriptable scope, T javaObject, Class staticType) {
        super(scope, javaObject, staticType);
	}

	public abstract boolean exist(String key);
	public abstract boolean exist(int index);
	public abstract boolean put(String key, Object value);
	public abstract boolean put(int index, Object value);
	public abstract Object get(String key);
	public abstract Object get(int index);
	public abstract boolean remove(String key);
	public abstract boolean remove(int index);
	public abstract Collection keys();

    public String getClassName() {
        return this.getClass().getName();
    }

    public boolean has(String name, Scriptable start) {
        return (super.has(name, start) || exist(name));
    }
    
    public boolean has(int index, Scriptable start) {
        return (super.has(index, start) || exist(String.valueOf(index)));
    }
    
    public Object get(String name, Scriptable start) {
    	if (exist(name)) {
            return get(name);
        } else if (super.has(name, start)) {
            return super.get(name, start);
        } else {
            return Scriptable.NOT_FOUND;
        }
    }

    public Object get(int index, Scriptable start) {
    	 if (exist(index)) {
             return get(index);
         }else if (super.has(index, start)) {
            return super.get(index, start);
        } else {
            return Scriptable.NOT_FOUND;
        }
    }

    public void put(String name, Scriptable start, Object value) {
        if (value instanceof NativeJavaObject) {
            value = ((NativeJavaObject)value).unwrap();
        }
        if(!put(name, value)){
        	super.put(name, start, value);
        }
    }

    public void put(int index, Scriptable start, Object value) {
    	if (value instanceof NativeJavaObject) {
            value = ((NativeJavaObject)value).unwrap();
        }
    	if(!put(index, value)){
    		super.put(index, start, value);
        }
    }

    public void delete(String id) {
    	if(!remove(id)){
        	super.delete(id);
        }
    }

    public void delete(int index) {
    	if(!remove(index)){
        	super.delete(index);
        }
    }

    public Object[] getIds() {
        return keys().toArray();
    }
    
    public Object getDefaultValue(Class typeHint) {
        return javaObject.toString();
    }

    public boolean hasInstance(Scriptable value) {
        Scriptable proto = value.getPrototype();
        while (proto != null) {
            if (proto.equals(this)) 
                return true;
            proto = proto.getPrototype();
        }

        return false;
    }
}

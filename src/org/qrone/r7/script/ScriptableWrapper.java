package org.qrone.r7.script;

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

    public String getClassName() {
        return this.getClass().getName();
    }

    public boolean has(String name, Scriptable start) {
        return (super.has(name, start) || (this instanceof Indexer && name.equals("length")) ||
        		(this instanceof Mapper && ((Mapper)this).exist(name)));
    }
    
    public boolean has(int index, Scriptable start) {
        return (super.has(index, start) || 
        		(this instanceof Indexer && ((Indexer)this).exist(index)));
    }
    
    public Object get(String name, Scriptable start) {
    	if (this instanceof Indexer && name.equals("length")){
    		return ((Indexer)this).size();
    	}else if (super.has(name, start)) {
            return super.get(name, start);
        } else if (this instanceof Mapper && ((Mapper)this).exist(name)) {
            return ((Mapper)this).get(name);
        }  else {
            return Scriptable.NOT_FOUND;
        }
    }

    public Object get(int index, Scriptable start) {
    	 if (super.has(index, start)) {
            return super.get(index, start);
        } else if(this instanceof Indexer && ((Indexer)this).exist(index)) {
            return ((Indexer)this).get(index);
        } else {
            return Scriptable.NOT_FOUND;
        }
    }

    public void put(String name, Scriptable start, Object value) {
    	if(this instanceof Mapper){
	        if (value instanceof NativeJavaObject) {
	            value = ((NativeJavaObject)value).unwrap();
	        }
	        ((Mapper)this).put(name, value);
    	}else{
    		super.put(name, start, value);
    	}
    }

    public void put(int index, Scriptable start, Object value) {
    	if(this instanceof Mapper){
	    	if (value instanceof NativeJavaObject) {
	            value = ((NativeJavaObject)value).unwrap();
	        }
	    	((Indexer)this).put(index, value);
    	}else{
    		super.put(index, start, value);
    	}
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

package org.qrone.r7.script.ext;

import java.lang.reflect.Array;

import org.qrone.r7.script.ScriptablePrototype;

public class ClassPrototype implements ScriptablePrototype<Class>{
	private Class parent;
	public ClassPrototype(Class parent) {
		this.parent = parent;
	}
	
	public Object[] newArray(int length){
		return (Object[])Array.newInstance(parent, length);
	}
}

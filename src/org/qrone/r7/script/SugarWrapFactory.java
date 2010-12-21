/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qrone.r7.script;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;
import org.qrone.r7.Extendable;

/**
 * Wraps Java objects by adding support for function extensions, which are
 * functions that extend existing Java objects at the Rhino level.
 */
public class SugarWrapFactory extends WrapFactory implements Extendable {
    private Map<Class, Class> wmap = new Hashtable<Class, Class>();
    private Map<Class, List<Class<? extends ScriptablePrototype>>> map
    	= new Hashtable<Class, List<Class<? extends ScriptablePrototype>>>();
    
    public SugarWrapFactory() {
        super();
    }
	
	private static Class getInterfaceGenericType(Class cls) {
		Type[] types = cls.getGenericInterfaces();
		for (int i = 0; i < types.length; i++) {
			if(types[i] instanceof ParameterizedType){
				ParameterizedType ty = (ParameterizedType) types[i];
				if(ty.getRawType().equals(ScriptablePrototype.class)){
					Type[] actualType = ty.getActualTypeArguments();
					if (actualType.length > 0 && actualType[0] instanceof Class) {
						return (Class) actualType[0];
					}
				}
			}
		}
		return null;
	}

	public void addExtension(Class wrapper) {
		if(ScriptablePrototype.class.isAssignableFrom(wrapper)){
			Class cls = getInterfaceGenericType(wrapper);
			addPrototypeClass(cls, wrapper);
		}
	}
	
	public void setWrapperClass(Class target, Class wrapper){
		wmap.put(target, wrapper);
	}
	
    public void addPrototypeClass(Class target, Class<? extends ScriptablePrototype> wrapper){
    	List<Class<? extends ScriptablePrototype>> l = map.get(target);
    	if(l == null){
    		l = new ArrayList<Class<? extends ScriptablePrototype>>();
    		map.put(target, l);
    	}
    	l.add(wrapper);
    }
    
    public Scriptable wrapAsJavaObject(Context cx, Scriptable scope,
    		Object javaObject, Class staticType) {
    	
		try {
			
			NativeJavaObject wrapper = new NativeJavaObject(scope, javaObject, staticType);
			for (Iterator<Class> i = map.keySet().iterator(); i.hasNext();) {
				Class type = i.next();
				if (type.isInstance(javaObject)) {
					List<Class<? extends ScriptablePrototype>> l = map.get(type);
					
					NativeJavaObject last = wrapper;
					for (Iterator<Class<? extends ScriptablePrototype>> iter = l.iterator(); iter.hasNext();) {
						Class<? extends ScriptablePrototype> cls = iter.next();
						
						ScriptablePrototype sjo = null;
						try{
							try{
								Constructor<? extends ScriptablePrototype> cr = 
									cls.getConstructor(Scriptable.class, type);
								sjo = cr.newInstance(scope, javaObject);
							}catch(Exception e1){
								Constructor<? extends ScriptablePrototype> cr = 
									cls.getConstructor(type);
								sjo = cr.newInstance(javaObject);
							}
						}catch(Exception e1){
							Constructor<? extends ScriptablePrototype> cr = 
								cls.getConstructor();
							sjo = cr.newInstance();
						}
						
						if(sjo instanceof Scriptable){
							return (Scriptable)sjo;
						}
						
						NativeJavaObject wsjo = new NativeJavaObject(scope, sjo, null);
						last.setPrototype(wsjo);
						last = wsjo;
					}
					return wrapper;
				}
			}
			return wrapper;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
    }

}

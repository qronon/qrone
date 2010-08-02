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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

/**
 * Wraps Java objects by adding support for function extensions, which are
 * functions that extend existing Java objects at the Rhino level.
 */
public class SugarWrapFactory extends WrapFactory {
    private Map<Class, List<Class<? extends ScriptableJavaObject>>> map
    	= new Hashtable<Class, List<Class<? extends ScriptableJavaObject>>>();
    
    public SugarWrapFactory() {
        super();
    }
    
    public void addWrapperClass(Class target, Class<? extends ScriptableJavaObject> wrapper){
    	List<Class<? extends ScriptableJavaObject>> l = map.get(target);
    	if(l == null){
    		l = new ArrayList<Class<? extends ScriptableJavaObject>>();
    		map.put(target, l);
    	}
    	l.add(wrapper);
    }
    
    public Scriptable wrapAsJavaObject(Context cx, Scriptable scope,
        Object javaObject, Class staticType) {
        if (javaObject instanceof Map) {
            return new ScriptableMap(cx, scope, javaObject, staticType);
        } else if (javaObject instanceof List) {
            return new ScriptableList(cx, scope, javaObject, staticType);
		} else {
			try {
				for (Iterator<Class> i = map.keySet().iterator(); i.hasNext();) {
					Class type = i.next();
					if (type.isInstance(javaObject)) {
						List<Class<? extends ScriptableJavaObject>> l = map.get(type);
						
						NativeJavaObject wrapper = new NativeJavaObject(scope, javaObject, staticType);
						NativeJavaObject last = wrapper;
						for (Iterator<Class<? extends ScriptableJavaObject>> iter = l.iterator(); iter.hasNext();) {
							Class<? extends ScriptableJavaObject> cls = iter.next();
							
							ScriptableJavaObject sjo = null;
							try{
								Constructor<? extends ScriptableJavaObject> cr = 
									cls.getConstructor(ContextPack.class);
								sjo = cr.newInstance(new ContextPack(cx, scope, javaObject, staticType, wrapper));
							}catch(Exception e){
								Constructor<? extends ScriptableJavaObject> cr = 
									cls.getConstructor();
								sjo = cr.newInstance();
								
							}
							NativeJavaObject wsjo = new NativeJavaObject(scope, sjo, null);
							last.setPrototype(wsjo);
							last = wsjo;
						}
						return wrapper;
					}
				}
				return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
    }

}

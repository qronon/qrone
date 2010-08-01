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
    private Map<Class, Class<? extends ScriptableJavaObject>> map
    	= new Hashtable<Class, Class<? extends ScriptableJavaObject>>();
    
    public SugarWrapFactory() {
        super();
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
						Constructor<? extends ScriptableJavaObject> cr = map.get(type)
								.getConstructor(Context.class, Scriptable.class, type, Class.class, Scriptable.class);
						NativeJavaObject wrapper = new NativeJavaObject(scope, javaObject, staticType);
						ScriptableJavaObject sjo = cr.newInstance(cx, scope, javaObject, staticType, wrapper);
						wrapper.setPrototype(new NativeJavaObject(scope, sjo, null));
						return wrapper;
					}
				}
				return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
			} catch (Exception e) {
			}
		}
		return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
    }

}

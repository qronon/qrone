package org.qrone.r7.script;

import java.util.Hashtable;
import java.util.Map;

import org.mozilla.javascript.Scriptable;

public class Scriptables {

    public static Map asMap(Scriptable s){
    	Map m = new Hashtable();
    	Object[] ids = s.getIds();
    	for (int i = 0; i < ids.length; i++) {
    		Object k = ids[i];
    		if(k instanceof String){
    			Object v = s.get(((String)k), s);
    			m.put(k, asJava(v));
    		}else if (k instanceof Number){
    			Object v = s.get(((Number)k).intValue(), s);
    			m.put(k, asJava(v));
    		}
		}
    	return m;
    }
    
    private static Object asJava(Object o){
    	if(o instanceof Scriptable){
    		return asMap((Scriptable)o);
    	}
    	return o;
    }
}

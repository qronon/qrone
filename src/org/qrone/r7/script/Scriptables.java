package org.qrone.r7.script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.qrone.r7.parser.JSDeck;

public class Scriptables {
	

    public static Map asMap(Scriptable s){
    	Map m = null;
    	if(s.get("window", s).equals(s)){
        	Scriptable scope = s.getPrototype();
        	s.setPrototype(null);
    		s.delete("window");
        	m = asMap(s, new HashSet());
        	s.setPrototype(scope);
        	s.put("window", s, s);
    	}else{
        	Scriptable scope = s.getPrototype();
        	s.setPrototype(null);
        	m = asMap(s, new HashSet());
        	s.setPrototype(scope);
    	}
    	return m;
    }
    
    public static Map asMap(Scriptable s, Set r){
    	Map m = new HashMap();
    	Object[] ids = s.getIds();
    	for (int i = 0; i < ids.length; i++) {
    		Object k = ids[i];
    		if(k instanceof String && !k.equals("prototype") && !k.equals("parentScope")){
    			Object v = s.get(((String)k), s);
    			m.put(k, asJava(v, r));
    		}else if (k instanceof Number){
    			Object v = s.get(((Number)k).intValue(), s);
    			m.put(k, asJava(v, r));
    		}
		}
    	return m;
    }

    public static List asList(NativeArray s, Set r){
    	long len = s.getLength();
    	List list = new ArrayList();
		for (int i = 0; i < len; i++) {
			list.add(i, asJava(s.get(i, s), r));
		}
    	return list;
    }
    
    private static Object asJava(Object o, Set r){
    	if(r.contains(o)){
    		return null;
    	}
    	r.add(o);
    	if(o instanceof NativeArray){
    		return asList((NativeArray)o, r);
    	}else if(o instanceof NativeJavaObject){
    		return asJava(((NativeJavaObject)o).unwrap(),r);
    	}else if(o instanceof Scriptable){
    		return asMap((Scriptable)o, r);
    	}else{
    		if(o.getClass().getName().startsWith("org.qrone")){
    			return o.getClass().getName();
    		}
    		return o;
    	}
    }
}

package org.qrone.r7.script.ext;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;
import org.qrone.r7.Extension;
import org.qrone.r7.parser.JSDeck;
import org.qrone.r7.script.Mapper;
import org.qrone.r7.script.ScriptableWrapper;

import org.mozilla.javascript.Context;

@Extension
public class MapWrapper extends ScriptableWrapper<Map> implements Mapper, Serializable {
    private static final long serialVersionUID = 7192070806139403748L;
    private Map map;
    private Scriptable scope;
    
    public MapWrapper(Scriptable scope, Map javaObject, Class staticType) {
        super(scope, javaObject, staticType);
        this.map = javaObject;
        this.scope = scope;
    }

	@Override
    public boolean exist(String key){
    	return map.containsKey(key);
    }

	@Override
    public Object get(String key){
		Context cx = JSDeck.getContext();
		Object o = map.get(key);
		if(o instanceof Collection)
			return cx.getWrapFactory().wrapAsJavaObject(cx, scope, o, null);
		return o;
    }
	
	@Override
    public void put(String key, Object value){
    	map.put(key, value);
    }

	@Override
	public Object remove(String key) {
		return map.remove(key);
	}
	
	@Override
	public Object[] getIds() {
		return map.keySet().toArray();
	}
}

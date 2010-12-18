package org.qrone.r7.script.ext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.qrone.r7.Extension;
import org.qrone.r7.parser.JSDeck;
import org.qrone.r7.script.Indexer;
import org.qrone.r7.script.ScriptableWrapper;

@Deprecated
public class ListWrapper extends ScriptableWrapper<List> implements Indexer, Serializable {
    private static final long serialVersionUID = -2221655313388687110L;
    private List list;
    private Scriptable scope;
    
    /*
    public ListWrapper() {
    }
    
    public ListWrapper(List list) {
        this.list = list;
    }
    */
    
    public ListWrapper(Scriptable scope, List javaObject, Class staticType) {
        super(scope, javaObject, staticType);
        this.list = javaObject;
        this.scope = scope;
    }

	@Override
	public boolean exist(int index) {
		try{
			return list.get(index) != null;
		}catch(IndexOutOfBoundsException e){
			return false;
		}
	}

	@Override
	public Object get(int index) {
		Context cx = JSDeck.getContext();
		Object o = list.get(index);
		if(o instanceof Collection)
			return cx.getWrapFactory().wrapAsJavaObject(cx, scope, o, null);
		return o;
	}

	@Override
	public void put(int index, Object value) {
		list.set(index, value);
	}

	@Override
	public Object remove(int index) {
		return list.remove(index);
	}

	@Override
	public Object[] getIds() {
		List<Integer> l = new ArrayList<Integer>();
		for (int i = 0; i < list.size(); i++) {
			if(list.get(i) != null)
				l.add(i);
		}
		return l.toArray();
	}

	@Override
	public int size() {
		return list.size();
	}
}


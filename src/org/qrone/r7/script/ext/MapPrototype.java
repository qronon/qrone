/*
  ==============================================================================

   This file is part of the MOA Lightweight Web Runner
   Copyright 2008 by kRAkEn/gORe's Jucetice Application Development

  ------------------------------------------------------------------------------

   MOA can be redistributed and/or modified under the terms of the
   GNU Lesser General Public License, as published by the Free Software Foundation;
   version 2 of the License only.

   MOA is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with MOA; if not, visit www.gnu.org/licenses or write to the
   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
   Boston, MA 02111-1307 USA

  ==============================================================================
*/

package org.qrone.r7.script.ext;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;

/**
 * ScriptableMap is a wrapper for java.util.Map instances that allows developers
 * to interact with them as if it were a native JavaScript object.
 * @desc Wraps a Java Map into a JavaScript Object
 */
public class MapPrototype extends ScriptableObject implements Wrapper, ScriptablePrototype<Map>{

    private Map<Object,Object> map;
    final static String CLASSNAME = "ScriptableMap";

    /*
    public static void init(Scriptable scope) throws NoSuchMethodException {
        Constructor<?> cnst = ScriptableMap.class.getConstructor(Object.class);
        FunctionObject jsCnst = new FunctionObject(CLASSNAME, cnst, scope);
        jsCnst.addAsConstructor(scope, new ScriptableMap(scope, null));
    }

    public ScriptableMap(Object obj) {
        if (obj instanceof Wrapper) {
            obj = ((Wrapper) obj).unwrap();
        }
        if (obj instanceof Map<?,?>) {
            this.map = (Map<Object,Object>) obj;
        } else if (obj == Undefined.instance) {
            this.map = new HashMap<Object,Object>();
        } else {
            throw new EvaluatorException("Invalid argument to ScriptableMap(): " + obj);
        }
    }
    */

    public MapPrototype(Scriptable scope, Map wappedMap) {
        super(scope, ScriptUtils.getClassOrObjectProto(scope, CLASSNAME));
        this.map = wappedMap;
    }

    public Object get(String name, Scriptable start) {
        if (map == null)
            return super.get(name, start);
        return get(name);
    }

    public Object get(int index, Scriptable start) {
        if (map == null)
            return super.get(index, start);
        return get(index);
    }

    public Object get(Object key) {
        Object value = map.get(key);
        if (value == null) {
            return Scriptable.NOT_FOUND;
        }
        return ScriptUtils.javaToJS(getParentScope(), value);
    }

    public boolean has(String name, Scriptable start) {
        if (map == null) {
            return super.has(name, start);
        } else {
            return map.containsKey(name);
        }
    }

    public boolean has(int index, Scriptable start) {
        if (map == null) {
            return super.has(index, start);
        } else {
            return map.containsKey(index);
        }
    }

    public void put(String name, Scriptable start, Object value) {
        if (map != null) {
            put(name, value);
        } else {
            super.put(name, start, value);
        }
    }

    public void put(int index, Scriptable start, Object value) {
        if (map != null) {
            put(index, value);
       } else {
            super.put(index, start, value);
        }
    }

    public Object put(Object key, Object value) {
        try {
            return map.put(key, Context.jsToJava(value, ScriptRuntime.ObjectClass));
        } catch (RuntimeException e) {
            Context.throwAsScriptRuntimeEx(e);
        }
        return null;
    }

    public void delete(String name) {
        if (map != null) {
            try {
                map.remove(name);
            } catch (RuntimeException e) {
                Context.throwAsScriptRuntimeEx(e);
            }
        } else {
            super.delete(name);
        }
    }

    public void delete(int index) {
        if (map != null) {
            try {
                map.remove(index);
            } catch (RuntimeException e) {
                Context.throwAsScriptRuntimeEx(e);
            }
        } else {
            super.delete(index);
        }
    }

    public Object[] getIds() {
        if (map == null) {
            return super.getIds();
        } else {
            return map.keySet().toArray();
        }
    }

    public String toString() {
        if (map == null)
            return super.toString();
        return map.toString();
    }

    public Object getDefaultValue(Class typeHint) {
        return toString();
    }
    
    public Object unwrap() {
        return map;
    }

    public Map<?,?> getMap() {
        return map;
    }

    public String getClassName() {
        return CLASSNAME;
    }
}
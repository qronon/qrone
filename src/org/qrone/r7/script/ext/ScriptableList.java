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

import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;
import org.qrone.r7.script.ScriptablePrototype;

/**
 * ScriptableList is a wrapper for java.util.List instances that allows developers 
 * to interact with them like it was a native JavaScript array.
 * @desc Wraps a Java List into a JavaScript Array
 */

public class ScriptableList extends NativeJavaObject implements ScriptablePrototype<List> {

    List<Object> list;
    static final String CLASSNAME = "ScriptableList";

    // Set up a custom constructor, for this class is somewhere between a host class and
    // a native wrapper, for which no standard constructor class exists
    public static void init(Scriptable scope) throws NoSuchMethodException {
        BaseFunction ctor = new BaseFunction(scope, ScriptableObject.getFunctionPrototype(scope)) {
            public Scriptable construct(Context cx, Scriptable scop, Object[] args) {
                if (args.length != 1) {
                    throw new EvaluatorException("Invalid number of arguments to ScriptableList()");
                }
                return new ScriptableList(scop, args[0]);
            }
        };
        scope.put(CLASSNAME, scope, ctor);
    }

    private ScriptableList(Scriptable scope, Object obj) {
        this.parent = scope;
        if (obj instanceof Wrapper) {
            obj = ((Wrapper) obj).unwrap();
        }
        if (obj instanceof List<?>) {
            this.javaObject = this.list = (List<Object>) obj;
        } else if (obj == Undefined.instance) {
            this.javaObject = this.list = new ArrayList<Object>();
        } else {
            throw new EvaluatorException("Invalid argument to ScriptableList(): " + obj);
        }
        this.staticType = this.list.getClass();
        initMembers();
    }

    public ScriptableList(Scriptable scope, List wrappedList) {
        super(scope, wrappedList, wrappedList.getClass());
        this.list = wrappedList;
    }

    public void delete(int index) {
        if (list != null) {
            try {
                list.remove(index);
            } catch (RuntimeException e) {
                throw Context.throwAsScriptRuntimeEx(e);
            }
        } else {
            super.delete(index);
        }
    }

    public Object get(int index, Scriptable start) {
        if (list == null)
            return super.get(index, start);
        try {
            if (index < 0 || index >= list.size()) {
                return Undefined.instance;
            } else {
                return ScriptUtils.javaToJS(getParentScope(), list.get(index));
            }
        } catch (RuntimeException e) {
            throw Context.throwAsScriptRuntimeEx(e);
        }
    }

    public boolean has(int index, Scriptable start) {
        if (list == null)
            return super.has(index, start);
        return index >= 0 && index < list.size();
    }

    public void put(int index, Scriptable start, Object value) {
        if (list != null) {
            try {
                list.set(index, Context.jsToJava(value, ScriptRuntime.ObjectClass));
            } catch (RuntimeException e) {
                Context.throwAsScriptRuntimeEx(e);
            }
        } else {
            super.put(index, start, value);
        }
    }
    
    public Object get(String name, Scriptable start) {
        if ("length".equals(name) && list != null) {
            return list.size();
        }
        return super.get(name, start);
    }

    public Object[] getIds() {
        if (list == null)
            return super.getIds();
        int size = list.size();
        Integer[] ids = new Integer[size];
        for (int i = 0; i < size; ++i) {
            ids[i] = i;
        }
        return ids;
    }

    public String toString() {
        if (list == null)
            return super.toString();
        return list.toString();
    }

    public Object getDefaultValue(Class typeHint) {
        return toString();
    }

    public Object unwrap() {
        return list;
    }

    public List<?> getList() {
        return list;
    }

    public String getClassName() {
        return CLASSNAME;
    }
}

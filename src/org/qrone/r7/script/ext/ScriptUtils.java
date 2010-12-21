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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

/**
 * A collection of Rhino utility methods.
 */
public class ScriptUtils {

	//==============================================================================
    /**
     * Coerce/wrap a java object to a JS object, and mask Lists and Maps
     * as native JS objects.
     * 
     * @param obj the object to coerce/wrap
     * @param scope the scope
     * @return the wrapped/masked java object
     */
	public static Object javaToJS(Scriptable scope, Object obj) {
        if (obj instanceof List<?>) {
            return new ScriptableList(scope, (List<?>) obj);
        } else if (obj instanceof Map<?,?>) {
            return new ScriptableMap(scope, (Map<?, ?>) obj);
        } else {
            return Context.javaToJS(obj, scope);
        }
    }

	//==============================================================================
    /**
     * Return a class prototype, or the object prototype if the class
     * is not defined.
     * 
     * @param scope the scope
     * @param className the class name
     * @return the class or object prototype
     */
    public static Scriptable getClassOrObjectProto(Scriptable scope, String className) {
        Scriptable proto = ScriptableObject.getClassPrototype(scope, className);
        if (proto == null) {
            proto = ScriptableObject.getObjectPrototype(scope);
        }
        return proto;
    }

	//==============================================================================
    /**
     * Make a scriptable object to be const and readonly in a scope
     * 
     * @param scope the scope
     * @param objectName the object name
     */
    public static void makeScriptableObjectConst (ScriptableObject scope, String objectName) {
    	/*
    	scope.setAttributes (objectName, ScriptableObject.READONLY |
    									 ScriptableObject.CONST |
    									 ScriptableObject.PERMANENT);
    	*/
    }
    
	//==============================================================================
    /**
     * 
     * @param na
     * @return
     */
    public static List<Object> unwrapNativeArray (final NativeArray na) {
        return new ArrayList<Object> () {{
            for (int i = 0; i < na.getLength(); ++i) {
                add(unwrapNative(na.get(i, null)));
            }
        }};
    }

    /**
     * 
     * @param na
     * @return
     */
    public static List<Object> unwrapPrototypeArray (final ScriptableObject sObj) {
        return new ArrayList<Object> () {{
            final List<Object> sObjIds = Arrays.asList(sObj.getAllIds());
            for (int i = 0; sObjIds.contains(i); ++i) {
                add(unwrapNative(sObj.get(i, null)));
            }
        }};
    }

    /**
     * 
     * @param sObj
     * @return
     */
    public static Map<String, Object> unwrapObject (final ScriptableObject sObj) {
        return new HashMap<String, Object> () {{ 
            for (Object id: sObj.getAllIds()) {
                put(id.toString(), unwrapNative(sObj.get(id.toString(), null)));
            }
        }};
    }

    /**
     * 
     * @param obj
     * @return
     */
    protected static Object unwrapNative (final Object obj)
    {
        if (obj instanceof NativeArray) {
            return unwrapNativeArray ((NativeArray) obj);
        }
        else if (obj instanceof Callable) {
        	return obj;
        }
        else if (obj instanceof ScriptableObject) {
            final ScriptableObject sObj = (ScriptableObject) obj;
            final List<Object> sObjIds = Arrays.asList (sObj.getAllIds());
            if (sObjIds.contains("keys")) { // a prototype enumerable/hash
                return unwrapObject (sObj);
            }
            else if (sObjIds.contains("flatten")) { // a prototype enumerable/array
                return unwrapPrototypeArray (sObj);
            }
            else {
            	return unwrapObject (sObj);
            }
        }
        else
        {
        	return obj;
        }
    }    

	//==============================================================================
    /**
     * 
     * @param scope
     * @param en
     * @return
     */
    public static Object[] getArray (Scriptable scope, NativeArray array) {
    	Object[] v = new Object[(int) array.getLength ()];
    	for (int i = 0; i < v.length; i++)
    		v [i] = array.get (i, array);
    	return v;
    }

    /**
     * 
     * @param scope
     * @param v
     * @return
     */
    public static Scriptable getJsArray (Scriptable scope, Vector<?> v) {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < v.size (); i++) {
            if (v.get (i) != null)
            	list.add (Context.toObject(v.get (i), scope));
        }
        Context cx = Context.getCurrentContext();
        return cx.newArray (scope, list.toArray());
    }

    /**
     * 
     * @param scope
     * @param en
     * @return
     */
    public static Scriptable getJsArray (Scriptable scope, Enumeration<?> en) {
        List<Object> list = new ArrayList<Object>();
        while (en.hasMoreElements()) {
            list.add(en.nextElement());
        }
        Context cx = Context.getCurrentContext();
        return cx.newArray (scope, list.toArray());
    }

    /**
     * 
     * @param scope
     * @param arr
     * @return
     */
    public static Scriptable getJsArray (Scriptable scope, Object[] arr) {
        Context cx = Context.getCurrentContext();
        if (arr == null)
            return cx.newArray(scope, 0);
        int length = arr.length;
        Scriptable array = cx.newArray(scope, length);
        for (int i = 0; i < length; i++) {
            if (arr[i] != null)
                array.put(i, array, Context.toObject(arr[i], scope));
        }
        return array;
    }

    /**
     * 
     * @param scope
     * @param arr
     * @return
     */
    public static Scriptable getWrappedJsArray (Scriptable scope, Object[] arr) {
        Context cx = Context.getCurrentContext();
        if (arr == null)
            return cx.newArray(scope, 0);
        int length = arr.length;
        Scriptable array = cx.newArray(scope, length);
        for (int i = 0; i < length; i++) {
            if (arr[i] != null)
                array.put(i, array, Context.javaToJS(arr[i], scope));
        }
        return array;
    }    
    
	//==============================================================================
    /**
     * 
     * @param scope
     * @param v
     * @return
     */
    public static Scriptable getJsObject (Context cx, Scriptable scope, Map<?,?> m)
    {
        Scriptable object = cx.newObject(scope);

        Object[] keys = m.keySet().toArray();
        for (int i = 0; i < keys.length; i++)
        {
            if (m.get (keys[i]) != null)
            {
            	object.put(Context.toString(keys[i]), object, Context.toObject(m.get (keys[i]), scope));
            }
        }
        
        return object;
    }
    
	//==============================================================================
    /**
     * Initialize and normalize the global variables and arguments on a thread scope.
     * @param args the arguments
     */
	public static void initArguments (Object[] args, Scriptable scope)
	{
    	if (args != null) {
            for (int i = 0; i < args.length; i++)
                args[i] = ScriptUtils.wrapArgument (args[i], scope);
        }
    }
    
    /**
     * Prepare a single property or argument value for use within rhino.
     * 
     * @param value the property or argument value
     * @param scope the scope
     * @return the object wrapped and wired for rhino
     */
    public static Object wrapArgument (Object value, Scriptable scope) {
    	if (value instanceof ScriptableObject) {
            ScriptableObject scriptable = ((ScriptableObject) value);
            scriptable.setPrototype (ScriptableObject.getClassPrototype (scope, scriptable.getClassName()));
            scriptable.setParentScope (scope);
            return scriptable;
        } else {
            return ScriptUtils.javaToJS(scope, value);
        }
    }
    
    /**
     * Make sure that number of arguments is valid.
     * 
     * @param args the argument array
     * @param min the minimum number of arguments
     * @param max the maximum number of arguments
     * @throws IllegalArgumentException if the number of arguments is not valid
     */
    public static void checkArguments(Object[] args, int min, int max)
    		throws IllegalArgumentException {
        if (min > -1 && args.length < min)
            throw new IllegalArgumentException();
        if (max > -1 && args.length > max)
            throw new IllegalArgumentException();
    }

    /**
     * Get an argument as ScriptableObject
     * 
     * @param args the argument array
     * @param pos the position of the requested argument
     * @return the argument as ScriptableObject
     * @throws IllegalArgumentException if the argument can't be converted to a map
     */
    public static ScriptableObject getScriptableArgument(Object[] args, int pos)
            throws IllegalArgumentException {
        if (pos >= args.length || args[pos] == null || args[pos] == Undefined.instance)
            return null;
        if (args[pos] instanceof ScriptableObject)
            return (ScriptableObject) args[pos];
        throw new IllegalArgumentException("Can't convert to ScriptableObject: " + args[pos]);
    }

    /**
     * Get an argument as string
     * 
     * @param args the argument array
     * @param pos the position of the requested argument
     * @return the argument as string
     */
    public static String getStringArgument(Object[] args, int pos, String defaultValue) {
        if (pos >= args.length || args[pos] == null || args[pos] == Undefined.instance) 
            return defaultValue;
        if (!(args[pos] instanceof String))
            throw new IllegalArgumentException("Expected String as argument " + (pos + 1));
        return (String) args[pos];
    }

    /**
     * Get an argument as integer
     * 
     * @param args the argument array
     * @param pos the position of the requested argument
     * @return the argument as string
     */
    public static int getIntArgument(Object[] args, int pos, int defaultValue) {
        if (pos >= args.length || args[pos] == null || args[pos] == Undefined.instance)
            return defaultValue;
        if (!(args[pos] instanceof Number))
            throw new IllegalArgumentException("Expected Number as argument " + (pos + 1));
        return ((Number) args[pos]).intValue();
    }
    
    /**
     * Get an argument as Map
     * 
     * @param args the argument array
     * @param pos the position of the requested argument
     * @return the argument as map
     * @throws IllegalArgumentException if the argument can't be converted to a map
     */
    public static Map<?,?> getMapArgument(Object[] args, int pos)
            throws IllegalArgumentException {
        if (pos >= args.length || args[pos] == null || args[pos] == Undefined.instance)
            return null;
        if (args[pos] instanceof Map<?,?>)
            return (Map<?,?>) args[pos];
        throw new IllegalArgumentException("Can't convert to java.util.Map: " + args[pos]);
    }

    /**
     * Get an argument as object
     * @param args the argument array
     * @param pos the position of the requested argument
     * @return the argument as object
     */
    public static Object getObjectArgument(Object[] args, int pos, Object defaultValue) {
        if (pos >= args.length || args[pos] == null || args[pos] == Undefined.instance)
            return defaultValue;
        return Context.jsToJava(args[pos], Object.class);
    }

    /**
     * Get an argument as function
     * @param args the argument array
     * @param pos the position of the requested argument
     * @return the argument as object
     */
    public static Function getFunctionArgument(Object[] args, int pos, Object defaultValue) {
        if (pos >= args.length || args[pos] == null || args[pos] == Undefined.instance)
            return null;
        if (args[pos] instanceof Function)
            return (Function) args[pos];
        throw new IllegalArgumentException("Can't convert to Function: " + args[pos]);
    }
    
	//==============================================================================
    /**
     * TODO
     */
/*
    public static void dumpScriptable (final String name, final Scriptable src)
    {
    	if (src != null)
    	{
		    Object[] ids = src.getIds();
			for (int i = 0; i < ids.length; i++)
			{
				String key = (String) ids[i];
				ScriptEngine.getLog().info (name + " (" + src.getClassName() + ") > " + key + " " + src.get (key, src));
			}
    	}
	}
*/
    
	//==============================================================================
    /**
     * 
     */
    protected static void copyContextDataToScope (String name, Scriptable scope, Context cx)
    {
    	scope.put (name, scope, ScriptUtils.wrapArgument (cx.getThreadLocal (name), scope));
    }
}

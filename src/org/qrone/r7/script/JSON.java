package org.qrone.r7.script;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.BSON;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.json.JsonParser;
import org.mozilla.javascript.json.JsonParser.ParseException;
import org.qrone.r7.ObjectConverter;
import org.qrone.r7.parser.HTML5Element;
import org.qrone.r7.parser.HTML5OM;
import org.qrone.r7.parser.HTML5Template;
import org.qrone.r7.parser.NodeLister;

public class JSON extends JSObject{
	
	public JSON(ServletScope ss) throws IOException{
		super(ss);
	}
	
	public Object parse(String json){
		JsonParser p = new JsonParser(ss.vm.getContext(), ss.scope);
		try {
			return p.parseValue(json);
		} catch (ParseException e) {
			return null;
		}
	}

	public String stringify(Object s){
		return ObjectConverter.to((Scriptable)s).toString();
	}
}

package org.qrone.r7.script.browser;

import java.io.IOException;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.json.JsonParser;
import org.mozilla.javascript.json.JsonParser.ParseException;
import org.qrone.r7.ObjectConverter;
import org.qrone.r7.parser.JSDeck;
import org.qrone.r7.script.ServletScope;
import org.qrone.r7.script.ServletScopeObject;

public class JSON extends ServletScopeObject{
	
	public JSON(ServletScope ss) throws IOException{
		super(ss);
	}
	
	public Object parse(String json){
		JsonParser p = new JsonParser(JSDeck.getContext(), ss.scope);
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

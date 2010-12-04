package org.qrone.r7.script.browser;

import java.io.IOException;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.json.JsonParser;
import org.mozilla.javascript.json.JsonParser.ParseException;
import org.qrone.r7.parser.JSDeck;

public class JSON{
	private Scriptable scope;
	public JSON(Scriptable scope) throws IOException{
		this.scope = scope;
	}
	
	public Object parse(String json){
		JsonParser p = new JsonParser(JSDeck.getContext(), scope);
		try {
			return p.parseValue(json);
		} catch (ParseException e) {
			return null;
		}
	}

	public String stringify(Object s){
		return net.arnx.jsonic.JSON.encode(s);
	}
}

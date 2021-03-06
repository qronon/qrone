package org.qrone.r7.format;

import java.io.IOException;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.json.JsonParser;
import org.mozilla.javascript.json.JsonParser.ParseException;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.script.ServerJSDeck;

import org.mozilla.javascript.Context;
public class JSON extends XFormat<Object>{
	private Scriptable scope;
	private Context context;
	public JSON(URIResolver r, Scriptable scope, Context context) throws IOException{
		super(r);
		this.scope = scope;
		this.context = context;
	}
	
	public Object parse(String json){
		JsonParser p = new JsonParser(context, scope);
		try {
			return p.parseValue(json);
		} catch (ParseException e) {
			return null;
		}
	}

	public String stringify(Object s){
		return net.arnx.jsonic.JSON.encode(s);
	}

	@Override
	public Object decode(String data) {
		return parse(data.toString());
	}

	@Override
	public String encode(Object data) {
		return stringify(data);
	}
}

package org.qrone.r7.parser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.qrone.r7.script.ServletScope;
import org.qrone.r7.script.Window;

public class JSOM {
	private URI uri;
	private JSDeck deck;
	private Script script;

	public JSOM(JSDeck deck) {
		this.deck = deck;
	}
	
	public void parser(URI uri) throws IOException{
		this.uri = uri;
			script = deck.getContext().compileReader(new InputStreamReader(
					deck.getResolver().getInputStream(uri), "utf8"), 
					uri.toString(), 0, null);
	}
	
	public Scriptable createScope(){
		Context cx = deck.getContext();
		Scriptable global = deck.getGlobalScope();
		return cx.newObject(global);
	}

	public void run(Scriptable scope) {
		script.exec(deck.getContext(), scope);
	}

	public void run(Scriptable scope, Object... prototypes) {
		Scriptable parent = scope;
		for (int i = 0; i < prototypes.length; i++) {
			Scriptable window = (Scriptable)Context.javaToJS(prototypes[i], scope);
			parent.setPrototype(window);
			parent = window;
		}
		
		scope.put("window", scope, scope);
		script.exec(deck.getContext(), scope);
	}

}

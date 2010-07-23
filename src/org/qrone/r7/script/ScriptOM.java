package org.qrone.r7.script;

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

public class ScriptOM {
	private URI uri;
	private ScriptDeck deck;

	public ScriptOM(ScriptDeck deck, URI uri) {
		this.uri = uri;
		this.deck = deck;
	}

	public void run(HttpServletRequest request, HttpServletResponse response) {
		Context cx = Context.enter();
		Scriptable scope = cx.initStandardObjects();
		ScriptableObject.putProperty(scope, "document", 
				Context.javaToJS(new Document(request, response), scope));
		
		try {
			Script script = cx.compileReader(new InputStreamReader(
					deck.getResolver().getInputStream(uri), "utf8"), 
					uri.toString(), 0, null);
			
			
			script.exec(cx, scope);
			
			
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			cx.exit();
		}
	}

}

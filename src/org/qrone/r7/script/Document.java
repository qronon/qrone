package org.qrone.r7.script;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.qrone.r7.parser.HTML5Element;
import org.qrone.r7.parser.HTML5OM;
import org.qrone.r7.parser.HTML5Template;
import org.qrone.r7.parser.NodeLister;

public class Document extends JSObject{
	private Writer writer;
	private HTML5Template t;
	
	public Document(ServletScope ss) throws IOException{
		super(ss);
		writer = ss.response.getWriter();
		
		if(ss.path.endsWith(".js")){
			String npath = ss.path.substring(0,ss.path.length()-".js".length());

			try {
				load(npath + ".html");
			} catch (URISyntaxException e) {}
		}
	}

	public void load(String uri) throws IOException, URISyntaxException{
		if(ss.resolver.exist(uri)){
			HTML5OM om = ss.deck.compile(new URI(uri));
			if(om != null){
				t = new HTML5Template(om);
			}else{
				throw new IOException();
			}
		}
	}

	public void set(String selector, String value){
		t.set(selector, value);
	}
	
	public void set(String selector, final Callable func){
		t.set(selector, new NodeLister() {
			@Override
			public void accept(HTML5Template t, HTML5Element e) {
				callJSFunction(func, t, t, e);
			}
		});
	}
	
	public void out() throws IOException{
		writer.append(t.out());
	}
	
	public void write(String out) throws IOException{
		writer.append(out);
	}
	
	public void flush() throws IOException{
		writer.flush();
	}
	
	public void close() throws IOException{
		writer.close();
	}
}

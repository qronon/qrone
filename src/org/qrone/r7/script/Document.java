package org.qrone.r7.script;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.qrone.r7.ObjectConverter;
import org.qrone.r7.parser.HTML5Element;
import org.qrone.r7.parser.HTML5OM;
import org.qrone.r7.parser.HTML5Template;
import org.qrone.r7.parser.NodeLister;

public class Document extends JSObject{
	private Writer writer;
	private HTML5Template t;
	
	public Location location;
	
	public Document(ServletScope ss) throws IOException{
		super(ss);
		writer = ss.writer;
	}
	
	public String getCookie(){
		return ss.request.getHeader("Cookie");
	}
	
	public HTML5Element getBody(){
		return t.getBody();
	}
	

	public void load(String uri) throws IOException, URISyntaxException{
		URI u = ss.uri.resolve(uri);
		if(ss.resolver.exist(u.toString())){
			HTML5OM om = ss.deck.compile(u);
			if(om != null){
				t = new HTML5Template(om, u);
			}else{
				throw new IOException();
			}
		}
	}

	public void load(HTML5Template template){
		t = template;
	}

	public void set(Object o){
		t.set(o);
	}

	public void set(String selector, final Object o){
		t.set(selector, o);
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

	public void write(Object out) throws IOException{
		writer.append(ObjectConverter.stringify(out));
	}
	
	public void write(String out) throws IOException{
		writer.append(out);
	}

	public void writeln(Object out) throws IOException{
		write(out);
		write("\n");
	}
	
	public void writeln(String out) throws IOException{
		write(out);
		write("\n");
	}
	
	public void flush() throws IOException{
		writer.flush();
	}
	
	public void close() throws IOException{
		writer.close();
	}
}

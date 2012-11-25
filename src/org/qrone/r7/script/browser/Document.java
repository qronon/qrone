package org.qrone.r7.script.browser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

import org.mozilla.javascript.xml.XMLObject;
import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.HTML5StreamWriter;
import org.qrone.r7.parser.HTML5Template;
import org.qrone.r7.script.Scriptables;

public class Document extends HTML5Template{
	private HttpServletRequest request;
	private Writer writer;
	private HTML5StreamWriter streamWriter;
	private User user;
	
	public Document(HttpServletRequest request, HttpServletResponse response, HTML5Deck deck, String uri, User user) throws IOException{
		super(deck, uri);
		this.request = request;
		this.writer = new BufferedWriter(response.getWriter());
		this.streamWriter = new HTML5StreamWriter(writer);
		this.user = user;
	}
	
	public String getCookie(){
		return request.getHeader("Cookie");
	}
	
	public void write(Object out) throws IOException{
		if(out instanceof String){
			writer.append((String)out);
		}else if(out instanceof HTML5Template){
			HTML5Template t = (HTML5Template)out;
			t.out(streamWriter, om.getDocument(), user.getTicket());
		}else if(out instanceof XMLObject){
			XMLObject xo = (XMLObject)out;
			String o = xo.callMethod(xo, "toXMLString", null).toString();
			writer.append(o);
		}else{
			writer.append(JSON.encode(Scriptables.asMap(out)));
		}
	}
	
	public void flush() throws IOException{
		if(loaded){
			super.out(streamWriter, om.getDocument(), user.getTicket());
		}
		writer.flush();
	}
	
	public void close() throws IOException{
		writer.close();
	}
}

package org.qrone.r7.script.browser;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.HTML5Template;

public class Document extends HTML5Template{
	private HttpServletRequest request;
	private PrintWriter writer;
	
	public Document(HttpServletRequest request, HttpServletResponse response, HTML5Deck deck, String uri, String ticket) throws IOException{
		super(deck, uri, ticket);
		this.request = request;
		this.writer = response.getWriter();
	}
	
	public String getCookie(){
		return request.getHeader("Cookie");
	}
	
	public void write(Object out) throws IOException{
		if(out instanceof String){
			writer.append((String)out);
		}else if(out instanceof HTML5Template){
			HTML5Template t = (HTML5Template)out;
			t.out();
			writer.append(t.serialize());
		}else{
			writer.append(JSON.encode(out));
		}
	}
	
	public void flush() throws IOException{
		if(loaded){
			super.out();
			writer.append(serialize());
		}
		writer.flush();
	}
	
	public void close() throws IOException{
		writer.close();
	}
}

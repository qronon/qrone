package org.qrone.r7.script.browser;

import java.io.IOException;

import org.qrone.r7.ObjectConverter;
import org.qrone.r7.parser.HTML5Template;
import org.qrone.r7.script.ServletScope;

public class Document extends HTML5Template{
	private ServletScope ss;
	
	public Document(ServletScope ss) throws IOException{
		super(ss.deck, ss.path + ".html");
		this.ss = ss;
	}
	
	public String getCookie(){
		return ss.request.getHeader("Cookie");
	}

	public void out() {
		super.out();
		ss.writer.append(toString());
	}
	
	public void write(Object out) throws IOException{
		if(out instanceof String)
			ss.writer.append((String)out);
		else
			ss.writer.append(ObjectConverter.stringify(out));
	}
	
	public void flush() throws IOException{
		ss.writer.flush();
	}
	
	public void close() throws IOException{
		ss.writer.close();
	}
}

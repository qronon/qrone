package org.qrone.r7.parser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import org.qrone.coder.QClass;
import org.qrone.coder.QFunc;
import org.qrone.coder.QState;
import org.qrone.coder.render.QLangJQuery;

public class HTML5ScriptWriter implements HTML5Writer{
	private Writer w;
	
	private QClass jqueryclass;
	private QFunc method;
	private QState jqueryhtml;
	
	public HTML5ScriptWriter(String uri){
		
		jqueryclass = new QClass(uri);
		method = jqueryclass.constructor();
		method.arg("String", "id");
		jqueryhtml = method.state().returns();
	}
	
	@Override
	public void append(String key, String value) {
		jqueryhtml.var("String", key);
	}
	
	@Override
	public void append(String str) {
		jqueryhtml.str(str);
	}
	
	@Override
	public void append(char c) {
		jqueryhtml.str(String.valueOf(c));
	}
	
	public String toString(){
		StringBuilder b = new StringBuilder();
		QLangJQuery q = new QLangJQuery();
		q.accept(jqueryclass);
		b.append(q.build());
		return b.toString();
	}

}

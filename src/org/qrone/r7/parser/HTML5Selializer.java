package org.qrone.r7.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.qrone.r7.handler.HTML5TagResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public abstract class HTML5Selializer extends HTML5Visitor{
	public final String[] noendtags = {"br", "img", "hr", "meta", "input", "embed", "area", "base", "col", "keygen", "link", "param", "source"};
	public final String[] nnendtags = {"li", "dt", "dd", "p", "tr", "td", "th", "rt", "rp", "optgroup", "option", "thread", "tfoot"};
	public final List<String> noendtaglist = Arrays.asList(noendtags);
	public final List<String> nnendtaglist = Arrays.asList(nnendtags);
	
	protected HTML5OM om;
	protected HTML5Writer b;
	protected String id;

	public void visit(HTML5OM om, Document e, String id, HTML5Writer w){
		this.om = om;
		this.b = w;
		this.id = id;
		visit(e);
	}

	public void visit(HTML5OM om, Element e, String id, HTML5Writer w){
		this.om = om;
		this.b = w;
		this.id = id;
		visit(e);
	}
	
	/*
	public void visit(HTML5OM om, Element e, final HTML5Writer... w){
		visit(om, e, new HTML5Writer() {
			@Override
			public void append(String key, String value) {
				for (int i = 0; i < w.length; i++) {
					w[i].append(key, value);
				}
			}
			
			@Override
			public void append(String str) {
				for (int i = 0; i < w.length; i++) {
					w[i].append(str);
				}
			}
			
			@Override
			public void append(char c) {
				for (int i = 0; i < w.length; i++) {
					w[i].append(c);
				}
			}
		});
	}
	*/

	protected String jsmin(String js){
		return jsmin(js, null);
	}
	
	protected String jsmin(String js, String replace){
		if(replace == null)
			return JSParser.compress(js, false);
		else
			return JSParser.compress(js, true).replace("__QRONE_PREFIX_NAME__", replace);
	}
	
	protected String cssmin(String css){
		return CSS3Parser.compress(css);
	}
	
	protected void out(String str){
		if(str == null) return;
		b.append(str);
	}

	protected void out(Element e){
		out(e,null);
	}
	protected void out(Element e, Delegate d){
		List<HTML5TagResult> r = om.getTagResult(e);
		if(r != null){
			List<HTML5TagResult> rr = new ArrayList<HTML5TagResult>(r);
			Collections.reverse(rr);
			for (Iterator<HTML5TagResult> iterator = r.iterator(); iterator
					.hasNext();) {
				out(iterator.next().prestart());
			}
			start(e);
			for (Iterator<HTML5TagResult> iterator = rr.iterator(); iterator
					.hasNext();) {
				out(iterator.next().poststart());
			}
			if(d != null){
				d.accept();
			}else{
				accept(e);
			}
			for (Iterator<HTML5TagResult> iterator = r.iterator(); iterator
					.hasNext();) {
				out(iterator.next().preend());
			}
			if(!noendtaglist.contains(e.getNodeName()))
				end(e);
			for (Iterator<HTML5TagResult> iterator = rr.iterator(); iterator
					.hasNext();) {
				out(iterator.next().postend());
			}
		}else{
			start(e);
			if(d != null){
				d.accept();
			}else{
				accept(e);
			}
			if(!noendtaglist.contains(e.getNodeName()))
				end(e);
		}
	}
	
	protected void writejs(String attr, String js){
		b.append(' ');
		b.append(attr);
		b.append('=');
		b.append('"');
		String[] jslist = js.split("__QRONE_ID__");
		for (int j = 0; j < jslist.length; j++) {
			if(j != 0){
				b.append("id",id);
			}
			b.append(jslist[j]);
		}
		b.append('"');
	}
	
	protected void start(Element e){
		b.append('<');
		b.append(e.getNodeName());

		NamedNodeMap map = e.getAttributes();
		for (int i = 0; i < map.getLength(); i++) {
			Node n = map.item(i);
			if(n.getNodeName().equals("id")){

				b.append(' ');
				b.append(n.getNodeName());
				b.append('=');
				b.append('"');
				b.append("id",id);
				write(n.getNodeValue());
				b.append('"');
				
			}else if(n.getNodeName().startsWith("on")){
				writejs(n.getNodeName(), jsmin(n.getNodeValue(),
						"qrone('" + om.getURI().toString() + "','__QRONE_ID__')"));
			}else if(n.getNodeName().equals("href") && n.getNodeValue().startsWith("javascript:")){
				String js = n.getNodeValue();
				if(js.startsWith("javascript:")){
					js = js.substring("javascript:".length());
					writejs(n.getNodeName(), "javascript:" + jsmin(js,
							"qrone('" + om.getURI().toString() + "','__QRONE_ID__')"));
				}
			}else{
				write((Attr)map.item(i));
			}
		}
		
		b.append('>');
	}
	
	protected void end(Element e){
		if(!noendtaglist.contains(e.getNodeName())
				&& !nnendtaglist.contains(e.getNodeName())){
			b.append('<');
			b.append('/');
			b.append(e.getNodeName());
			b.append('>');
		}
	}
	
	protected void write(String attr, String value){
		b.append(' ');
		b.append(attr);
		b.append('=');
		b.append('"');
		write(value);
		b.append('"');
	}
	
	protected void write(Attr attr){
		b.append(' ');
		b.append(attr.getNodeName());
		b.append('=');
		b.append('"');
		write(attr.getNodeValue());
		b.append('"');
	}

	protected void writeraw(String str){
		char[] ch = str.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			switch (ch[i]) {
			case '<':
				b.append("&lt;");
				break;
			case '>':
				b.append("&gt;");
				break;
			case '"':
				b.append("&quot;");
				break;
			case '&':
				b.append("&amp;");
				break;
			case ' ':
			case '\u00A0':
				b.append("&nbsp;");
				break;
			case '\0':
				break;
			default:
				b.append(ch[i]);
				break;
			}
		}
	}
	
	protected void write(String str){
		boolean white = false;
		char[] ch = str.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			switch (ch[i]) {
			case '\t':
			case '\r':
			case '\n':
			case ' ':
				if (!white) {
					b.append(' ');
					white = true;
				}
				break;
			case '<':
				b.append("&lt;");
				break;
			case '>':
				b.append("&gt;");
				break;
			case '"':
				b.append("&quot;");
				break;
			case '&':
				b.append("&amp;");
				break;
			case '\u00A0':
				b.append("&nbsp;");
				break;
			case '\0':
				break;
			default:
				b.append(ch[i]);
				white = false;
				break;
			}
		}
	}
}

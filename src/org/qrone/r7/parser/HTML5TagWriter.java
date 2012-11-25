package org.qrone.r7.parser;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public abstract class HTML5TagWriter extends HTML5Visitor {

	public static final String[] noendtags = {"br", "img", "hr", "meta", "input", "embed", "area", "base", "col", "keygen", "link", "param", "source"};
	public static final String[] nnendtags = {"li", "dt", "dd", "p", "tr", "td", "th", "rt", "rp", "optgroup", "option", "thread", "tfoot"};
	public static final Set<String> noendtaglist = new HashSet<String>();
	protected HTML5Writer b;
	protected String id;
	protected URI uri;
	protected String ticket;

	public HTML5TagWriter(HTML5Writer b, String id, URI uri, String ticket) {
		super();
		this.b = b;
		this.id = id;
		this.uri = uri;
		this.ticket = ticket;
	}
	
	protected void append_pre(String str) {
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

	protected void append(String str) {
		if(str == null) return;
		b.append(str);
	}
	
	protected String escape(String str) {
		StringBuilder b = new StringBuilder();
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
		return b.toString();
	}

	protected void start(Element e) {
		
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
				
				String rawid = n.getNodeValue();
				if(rawid.startsWith("qrone.")){
					b.append("qrone.");
					b.append("id",id);
					b.append(".");
					b.append(escape(rawid.substring("qrone.".length())));
				}else{
					b.append(escape(rawid));
				}
				b.append('"');
			}else if(n.getNodeValue().indexOf("__QRONE_ID__") >= 0 && (
						n.getNodeName().startsWith("on") || (
							n.getNodeName().startsWith("href") 
							&& n.getNodeValue().startsWith("javascript:")
					))){
				
				b.append(' ');
				b.append(n.getNodeName());
				b.append('=');
				b.append('"');
				String[] jslist = n.getNodeValue().split("__QRONE_ID__");
				for (int j = 0; j < jslist.length; j++) {
					if(j != 0){
						b.append(escape("qrone(\"" + uri.toString() + "\",\""));
						b.append("id",id);
						b.append(escape("\")"));
					}
					b.append(jslist[j]);
				}
				b.append('"');
				
			}else{
				Attr attr = (Attr)n;
				b.append(' ');
				b.append(attr.getNodeName());
				b.append('=');
				b.append('"');
				b.append(escape(attr.getNodeValue()));
				b.append('"');
			}
		}
		b.append('>');
	}

	protected void end(Element e) {
		if(!noendtaglist.contains(e.getNodeName())){
			b.append('<');
			b.append('/');
			b.append(e.getNodeName());
			b.append('>');
		}
	}
}
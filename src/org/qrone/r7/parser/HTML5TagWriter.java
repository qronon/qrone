package org.qrone.r7.parser;

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
	protected String ticket;

	public HTML5TagWriter(HTML5Writer b, String id, String ticket) {
		super();
		this.b = b;
		this.id = id;
		this.ticket = ticket;
	}

	protected void append_js(String attr, String js) {
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
		if(e.hasAttribute("qrone.starttag")){
			b.append(e.getAttribute("qrone.starttag"));
		}else{
			
			HTML5StringWriter bw = new HTML5StringWriter();
			bw.append('<');
			bw.append(e.getNodeName());
		
			NamedNodeMap map = e.getAttributes();
			for (int i = 0; i < map.getLength(); i++) {
				Node n = map.item(i);
				if(n.getNodeName().equals("id")){
		
					bw.append(' ');
					bw.append(n.getNodeName());
					bw.append('=');
					bw.append('"');
					
					String rawid = n.getNodeValue();
					if(rawid.startsWith("qrone.")){
						bw.append("qrone.");
						bw.append("id",id);
						bw.append(".");
						bw.append(escape(rawid.substring("qrone.".length())));
					}else{
						bw.append(escape(rawid));
					}
					bw.append('"');
				}else if(!n.getNodeName().startsWith("qrone.")){
					Attr attr = (Attr)n;
					bw.append(' ');
					bw.append(attr.getNodeName());
					bw.append('=');
					bw.append('"');
					bw.append(escape(attr.getNodeValue()));
					bw.append('"');
				}
			}
			bw.append('>');

			e.setAttribute("qrone.starttag",bw.toString());
			b.append(bw.toString());
		}
	}

	protected void end(Element e) {
		if(e.hasAttribute("qrone.endtag")){
			b.append(e.getAttribute("qrone.endtag"));
		}else{
			StringBuilder bs = new StringBuilder();
			if(!noendtaglist.contains(e.getNodeName())){
				bs.append('<');
				bs.append('/');
				bs.append(e.getNodeName());
				bs.append('>');
			}
			
			e.setAttribute("qrone.endtag",bs.toString());
			b.append(bs.toString());
		}
	}
}
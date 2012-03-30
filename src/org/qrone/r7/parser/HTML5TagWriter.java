package org.qrone.r7.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.qrone.r7.tag.HTML5TagResult;
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
	protected HTML5Template t;
	protected HTML5OM om;
	protected String ticket;

	public HTML5TagWriter() {
		super();
	}

	protected void writejs(String attr, String js) {
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

	protected void write(String attr, String value) {
		b.append(' ');
		b.append(attr);
		b.append('=');
		b.append('"');
		write(value);
		b.append('"');
	}

	protected void write(Attr attr) {
		b.append(' ');
		b.append(attr.getNodeName());
		b.append('=');
		b.append('"');
		write(attr.getNodeValue());
		b.append('"');
	}

	protected void writeraw(String str) {
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

	protected void writec(String str) {
		if(str == null) return;
		b.append(str);
	}
	
	protected void write(String str) {
		if(str == null) return;
		
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
		this.b.append(b.toString());
	}

	protected void start(Element e) {
		b.append('<');
		if(e.hasAttribute("tag"))
			b.append(e.getAttribute("tag"));
		else
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
					write("qrone.");
					b.append("id",id);
					write(".");
					write(rawid.substring("qrone.".length()));
				}else{
					write(rawid);
				}
				b.append('"');
			}else{
				write((Attr)map.item(i));
			}
		}
		
		b.append('>');
	}

	protected void end(Element e) {
		if(!noendtaglist.contains(e.getNodeName())){
			b.append('<');
			b.append('/');
			if(e.hasAttribute("tag"))
				b.append(e.getAttribute("tag"));
			else
				b.append(e.getNodeName());
			b.append('>');
		}
	}


	protected void start(Element e, List<HTML5TagResult> r) {
		if(r != null){
			List<HTML5TagResult> rr = new ArrayList<HTML5TagResult>(r);
			for (Iterator<HTML5TagResult> iterator = r.iterator(); iterator
					.hasNext();) {
				writec(iterator.next().prestart(ticket));
			}
			
			start(e);
			
			Collections.reverse(rr);
			for (Iterator<HTML5TagResult> iterator = rr.iterator(); iterator
					.hasNext();) {
				writec(iterator.next().poststart(ticket));
			}
		}else{
			start(e);
		}
	}

	protected void end(Element e, List<HTML5TagResult> r) {
		if(r != null){
			List<HTML5TagResult> rr = new ArrayList<HTML5TagResult>(r);
			for (Iterator<HTML5TagResult> iterator = r.iterator(); iterator
					.hasNext();) {
				writec(iterator.next().preend(ticket));
			}
			if(!noendtaglist.contains(e.getNodeName()))
				end(e);
			Collections.reverse(rr);
			for (Iterator<HTML5TagResult> iterator = rr.iterator(); iterator
					.hasNext();) {
				writec(iterator.next().postend(ticket));
			}
		}else{
			end(e);
		}
	}

}
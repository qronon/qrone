package org.qrone.r7.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.qrone.r7.tag.HTML5TagResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public abstract class HTML5Selializer extends HTML5Visitor{
	public static final String[] noendtags = {"br", "img", "hr", "meta", "input", "embed", "area", "base", "col", "keygen", "link", "param", "source"};
	public static final String[] nnendtags = {"li", "dt", "dd", "p", "tr", "td", "th", "rt", "rp", "optgroup", "option", "thread", "tfoot"};
	public static final Set<String> noendtaglist = new HashSet<String>();
	static{
		noendtaglist.addAll(Arrays.asList(noendtags));
		noendtaglist.addAll(Arrays.asList(nnendtags));
	}
	protected HTML5OM om;
	protected HTML5Writer b;
	protected String id;

	public void visit(HTML5OM om, Document e, String id, HTML5Writer w){
		this.om = om;
		this.b = w;
		this.id = id;
		visit(e);
	}

	public void visit(HTML5OM om, Node e, String id, HTML5Writer w){
		this.om = om;
		this.b = w;
		this.id = id;
		visit(e);
	}
	
	public HTML5Writer getWriter(){
		return b;
	}
	
	protected void out(String str){
		if(str == null) return;
		b.append(str);
	}
	
	protected void out(HTML5Element e5, NodeProcessor template){
		Element e = e5.get();
		List<HTML5TagResult> r = om.getTagResult(e);
		if(r != null){
			List<HTML5TagResult> rr = new ArrayList<HTML5TagResult>(r);
			for (Iterator<HTML5TagResult> iterator = r.iterator(); iterator
					.hasNext();) {
				iterator.next().process(e5);
			}
			
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

			if(e5.hasContent()){
				e5.accept(template);
				///HTML5Template t = template.newTemplate();
				//e5.accept(t);
				//getWriter().append(t);
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
			if(e5.hasContent()){
				e5.accept(template);
				//HTML5Template t = template.newTemplate();
				//e5.accept(t);
				//getWriter().append(t);
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
				b.append("id",id);
				write(n.getNodeValue());
				b.append('"');
			}else{
				write((Attr)map.item(i));
			}
		}
		
		b.append('>');
	}
	
	protected void end(Element e){
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
}

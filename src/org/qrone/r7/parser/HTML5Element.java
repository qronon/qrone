package org.qrone.r7.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.qrone.r7.script.browser.Function;
import org.qrone.r7.tag.HTML5TagResult;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;

public class HTML5Element implements HTML5Node{
	private HTML5OM om;
	private HTML5Template t;
	private Element e;
	private Element oe;
	
	public Object content;
	public List before;
	public List prepend;
	public List append;
	public List after;
	
	private Set<Node> remove;
	
	public HTML5Element(HTML5OM om, HTML5Template t, Element e){
		this.om = om;
		this.t = t;
		this.e = e;
	}
	
	public HTML5OM getOM(){
		return om;
	}
	
	public HTML5Node clone(){
		return new HTML5Element(om, t.newTemplate(), get());
	}
	
	public Element get(boolean override){
		if(override){
			if(oe == null) oe = (Element) e.cloneNode(false);
			return oe;
		}
		return e;
	}

	public Set<Node> getNodeSet(){
		Set<Node> set = new HashSet<Node>();
		set.add(get());
		return set;
	}
	
	public Element get(){
		if(oe != null) return oe;
		return e;
	}
	
	
	public String getAttribute(String name){
		return get().getAttribute(name);
	}

	public void setAttribute(String name, String value){
		get(true).setAttribute(name, value);
	}
	
	public String getProperty(String prop){
		CSS3Value v = getPropertyValue(prop);
		return v != null ? v.getValue() : null;
	}
	
	public CSS3Value getPropertyValue(String prop){
		CSS3Value value = null;
		CSS3Value imporantValue = null;
		String attr = get().getAttribute("style");
		if(attr != null){
			try {
				CSSStyleDeclaration style = CSS3Parser.parsestyle(attr);
				CSSValue v = style.getPropertyCSSValue(prop);
				if(v != null){
					CSS3Value v3 = new CSS3Value(getOM().getURI(), style, v, prop);
					if(v3.isImportant())
						imporantValue = v3;
					else
						value = v3;
				}
			} catch (IOException e1) {
			}
		}
		return imporantValue != null ? imporantValue : value;
	}
	
	public void setProperty(String prop, String value){
		String attr = get().getAttribute("style");
		if(attr != null){
			try {
				CSSStyleDeclaration style = CSS3Parser.parsestyle(attr);
				style.setProperty(prop, value, null);
				get(true).setAttribute("style", style.getCssText());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}else{
			get(true).setAttribute("style", prop + ":" + value + ";");
		}
	}

	public void renameProperty(String prop, String newprop){
		
		String attr = get().getAttribute("style");
		if(attr != null){
			try {
				CSSStyleDeclaration style = CSS3Parser.parsestyle(attr);
				CSSValue v = style.getPropertyCSSValue(prop);
				if(v != null){
					style.setProperty(newprop, v.getCssText(), style.getPropertyPriority(prop));
					style.removeProperty(prop);
					get(true).setAttribute("style", style.getCssText());
				}
			} catch (IOException e1) {
			}
		}
	}
	
	public void removeProperty(String prop){
		
		String attr = get().getAttribute("style");
		if(attr != null){
			try {
				CSSStyleDeclaration style = CSS3Parser.parsestyle(attr);
				CSSValue v = style.getPropertyCSSValue(prop);
				if(v != null){
					style.removeProperty(prop);
					get(true).setAttribute("style", style.getCssText());
				}
			} catch (IOException e1) {
			}
		}
	}
	
	public boolean hasContent(){
		return content != null;
	}
	
	public HTML5Template getDocument(){
		return t;
	}

	public String html() {
		if(t == null) return null;
		
		HTML5StringWriter w = new HTML5StringWriter();
		t.out(w, this);
		return w.toString();
	}

	public HTML5Node html(HTML5Template html) {
		this.content = html;
		return this;
	}
	
	public HTML5Node html(HTML5Node html) {
		this.content = html;
		return this;
	}

	public HTML5Node html(Function html) {
		this.content = html.call(t);
		return this;
	}
	
	public HTML5Node html(String html) {
		this.content = html;
		return this;
	}
	
	public HTML5Node css(String prop, String value) {
		setProperty(prop, value);
		return this;
	}

	public String css(String prop) {
		return getProperty(prop);
	}

	public HTML5Node attr(String prop, String value) {
		setAttribute(prop, value);
		return this;
	}

	public String attr(String prop) {
		return getAttribute(prop);
	}

	public HTML5Node addClass(String cls) {
		String classes = getAttribute("class");
		if(classes.trim().length() > 0){
			setAttribute("class", classes.trim() + " " + cls);
		}else{
			setAttribute("class", cls);
		}
		return this;
	}

	public HTML5Node removeClass(String cls) {
		StringBuffer buf = new StringBuffer();
		String[] cs = getAttribute("class").split(" ");
		for (int i = 0; i < cs.length; i++) {
			if(!cs[i].equals(cls))
				buf.append(cs[i]);
		}
		setAttribute("class", buf.toString().trim());
		return this;
	}

	public HTML5Node removeChild(HTML5Node o){
		if(remove == null)
			remove = new HashSet<Node>();
		
		Object s = o.get();
		if(s instanceof Set){
			remove.addAll((Set<Node>)s);
		}else if(s instanceof Element){
			remove.add((Element)s);
		}
		return this;
	}
	
	public HTML5Node remove(HTML5Node node){
		return removeChild(node);
	}
	
	public HTML5Node remove(){
		HTML5Node node = t.override((Element)this.get().getParentNode());
		node.removeChild(this);
		return this;
	}
	
	private void remove(Element e){
		Node parent = e.getParentNode();
		if(parent instanceof Element){
			HTML5Element e5 = t.override((Element)parent);
			e5.removeChild(this);
		}
	}
	
	public HTML5Node append(String o){
		if(append == null)
			append = new ArrayList();
		append.add(o);
		return this;
	}

	public HTML5Node appendChild(HTML5Node o){
		return append(o);
	}
	
	public HTML5Node append(HTML5Node o){
		if(append == null)
			append = new ArrayList();
		append.add(o);
		return this;
	}
	
	public HTML5Node append(Function o){
		if(append == null)
			append = new ArrayList();
		append.add(o);
		return this;
	}

	public HTML5Node prepend(String o){
		if(prepend == null)
			prepend = new ArrayList();
		prepend.add(o);
		return this;
	}

	public HTML5Node prepend(HTML5Node o){
		if(prepend == null)
			prepend = new ArrayList();
		prepend.add(o);
		return this;
	}
	
	public HTML5Node prepend(Function o){
		if(prepend == null)
			prepend = new ArrayList();
		prepend.add(o);
		return this;
	}
	
	@Override
	public HTML5Node each(Function func) {
		func.call(this);
		return this;
	}

	@Override
	public HTML5Node select(String o) {
		return t.select(o, this);
	}

	@Override
	public HTML5Node repeat(List l){
		return repeat(l, null);
	}

	@Override
	public HTML5Node repeat(final List l, final Function f){
		List c = new LinkedList();
		for (Iterator iterator = l.iterator(); iterator.hasNext();) {
			if(f != null){
				HTML5Node cloneNode = HTML5Element.this.clone();
				f.call(cloneNode.getDocument(), iterator.next());
				c.add(cloneNode);
			}else{
				c.add(iterator.next());
			}
		}
		content = c;
		return this;
	}

}

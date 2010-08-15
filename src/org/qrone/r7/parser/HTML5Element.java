package org.qrone.r7.parser;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;

public class HTML5Element {
	private HTML5OM om;
	private Element e;
	private Element oe;
	private Map<Node, List<CSS3Rule>> map;
	
	private Object content;
	
	
	public HTML5Element(HTML5OM om, Element e){
		this.om = om;
		this.e = e;
		this.map = om.getCSSRuleMap();
	}
	 
	public HTML5OM getOM(){
		return om;
	}

	public Element get(boolean override){
		if(override){
			if(oe == null) oe = (Element) e.cloneNode(false);
			return oe;
		}
		return get();
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
		List<CSS3Rule> l = map.get(e);
		CSS3Value value = null;
		CSS3Value imporantValue = null;
		if(l != null){
			for (Iterator<CSS3Rule> iter = l.iterator(); iter.hasNext();) {
				CSS3Rule rule = iter.next();
				CSS3Value v = rule.getProperty(prop);
				if(v != null){
					if(v.isImportant())
						imporantValue = v;
					else
						value = v;
				}
			}
		}
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
		List<CSS3Rule> l = map.get(e);
		if(l != null){
			for (Iterator<CSS3Rule> iter = l.iterator(); iter.hasNext();) {
				CSS3Rule rule = iter.next();
				CSS3Value v = rule.getProperty(prop);
				if(v != null){
					rule.setProperty(newprop, v.getValue(), v.isImportant());
					rule.removeProperty(prop);
				}
			}
		}
		
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
		List<CSS3Rule> l = map.get(e);
		if(l != null){
			for (Iterator<CSS3Rule> iter = l.iterator(); iter.hasNext();) {
				iter.next().removeProperty(prop);
			}
		}
		
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

	public void html(Object html) {
		this.content = html;
	}

	public void accept(HTML5Template t) {
		if(content instanceof NodeLister){
			((NodeLister)content).accept(t, this);
		}else{
			t.append(content.toString());
		}
	}

	public void css(String prop, String value) {
		setProperty(prop, value);
	}

	public void css(String prop) {
		getAttribute(prop);
	}

	public void attr(String prop, String value) {
		setAttribute(prop, value);
	}

	public void attr(String prop) {
		getAttribute(prop);
	}

	public void addClass(String cls) {
		String classes = getAttribute("class");
		if(classes.trim().length() > 0){
			setAttribute("class", classes.trim() + " " + cls);
		}else{
			setAttribute("class", cls);
		}
	}

	public void removeClass(String cls) {
		StringBuffer buf = new StringBuffer();
		String[] cs = getAttribute("class").split(" ");
		for (int i = 0; i < cs.length; i++) {
			if(!cs[i].equals(cls))
				buf.append(cs[i]);
		}
		setAttribute("class", buf.toString().trim());
	}
}

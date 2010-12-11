package org.qrone.r7.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.qrone.r7.parser.HTML5NodeSet.Delegate;
import org.qrone.r7.script.browser.Function;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;

public class HTML5Element implements HTML5Node{
	private HTML5OM om;
	private HTML5Template t;
	private Element e;
	private Element oe;
	private Map<Node, List<CSS3Rule>> map;
	
	private Object content;
	
	private List append;
	private List prepend;
	
	
	public HTML5Element(HTML5OM om, HTML5Template t, Element e){
		this.om = om;
		this.t = t;
		this.e = e;
		this.map = om.getCSSRuleMap();
	}
	 
	public HTML5OM getOM(){
		return om;
	}
	
	public HTML5Node clone(){
		return new HTML5Element(om, t.newTemplate(), (Element)e.cloneNode(false));
	}
	
	public void accept(HTML5Template t) {
		int index = 0;
		if(prepend != null){
			for (Object o : prepend) {
				appendTo(t,o,index++,null);
			}
		}
		
		appendTo(t,content,-1,null);

		index = 0;
		if(append != null){
			for (Object o : append) {
				appendTo(t,o,index++,null);
			}
		}
	}
	
	public void appendTo(final HTML5Template t, Object o, int index, String html){
		if(o instanceof HTML5Template){
			HTML5Template tt = (HTML5Template)o;
			tt.out(tt.getBody());
			t.append(tt);
		}else if(o instanceof HTML5Node){
			HTML5Node e = (HTML5Node)o;
			t.append(e.html());
		}else if(o instanceof Function){
			appendTo(t, ((Function)o).call(index, html), index, html);
		}else{
			t.append(o.toString());
		}
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

	public String html() {
		if(t == null) return null;
		HTML5Template tt = t.newTemplate();
		tt.out(this);
		return tt.toString();
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
		this.content = html;
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

	public HTML5Node css(String prop) {
		getAttribute(prop);
		return this;
	}

	public HTML5Node attr(String prop, String value) {
		setAttribute(prop, value);
		return this;
	}

	public HTML5Node attr(String prop) {
		getAttribute(prop);
		return this;
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

	public HTML5Node append(String o){
		if(append == null)
			append = new ArrayList();
		append.add(o);
		return this;
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
}

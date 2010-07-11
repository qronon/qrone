package org.qrone.r7.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.qrone.r7.handler.HTML5TagHandler;
import org.qrone.r7.handler.HTML5TagResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.css.CSSMediaRule;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.stylesheets.MediaList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import se.fishtank.css.selectors.NodeSelectorException;
import se.fishtank.css.selectors.dom.DOMNodeSelector;

public class HTML5OM {
	protected Document document;
	protected Element body;
	private Map<Node, List<CSSStyleRule>> map = new Hashtable<Node, List<CSSStyleRule>>();
	
	protected List<CSSStyleSheet> stylesheets = new LinkedList<CSSStyleSheet>();
	protected List<String> javascripts = new LinkedList<String>();

	protected List<String> requires = new LinkedList<String>();
	protected List<Element> jslibs = new LinkedList<Element>();
	protected List<Element> csslibs = new LinkedList<Element>();

	private Map<String, String> metamap = new Hashtable<String, String>();
	
	private DOMNodeSelector nodeselector;

	private List<HTML5TagHandler> handlers = new ArrayList<HTML5TagHandler>();
	private Map<Element, List<HTML5TagResult>> extmap = new Hashtable<Element, List<HTML5TagResult>>();

	private String className;
	
	public void addTagHandler(HTML5TagHandler h){
		handlers.add(h);
	}
	
	public Set<Node> select(String selector) throws NodeSelectorException{
		return nodeselector.querySelectorAll(selector);
	}
	
	public void parseStyleSheet(String css) throws IOException{
		CSSStyleSheet s = CSS2Parser.parse(css);
		CSS2Visitor v = new CSS2Visitor(){

			@Override
			public void visit(CSSMediaRule rule){
				MediaList list = rule.getMedia();
				if(list.getMediaText().indexOf("all") >= 0 || list.getMediaText().indexOf("screen") >= 0){
					accept(rule);
				}
			}
			
			@Override
			public void visit(CSSStyleRule style) {
				try {
					Set<Node> set = select(style.getSelectorText());
					for (Iterator<Node> i = set.iterator(); i
							.hasNext();) {
						Node node = i.next();
						List<CSSStyleRule> list = map.get(node);
						if(list == null){
							list = new ArrayList<CSSStyleRule>();
							map.put(node, list);
						}
						//System.out.println(node.getAttributes().getNamedItem("class"));
						list.add(style);
					}
				} catch (NodeSelectorException e) {
				}
			}
		};
		v.visit(s);
		stylesheets.add(s);
	}
	
	public void parse(String name, InputSource source) throws SAXException, IOException{
		className = name;
		document = HTML5Parser.parse(source);
		nodeselector = new DOMNodeSelector(document);
		HTML5Visitor visitor = new HTML5Visitor() {
			
			private boolean inBody = false;
			private boolean inScript = false;
			private boolean inStyle = false;
			
			@Override
			public void visit(Element e) {
				for (Iterator<HTML5TagHandler> i = handlers.iterator(); i
						.hasNext();) {
					HTML5TagHandler h = i.next();
					HTML5TagResult r = h.process(new HTML5Element(HTML5OM.this, e));
					if(r != null){
						List<HTML5TagResult> l = extmap.get(e);
						if(l == null){
							l = new ArrayList<HTML5TagResult>();
							extmap.put(e, l);
						}
						l.add(r);
					}
				}
				
				if(e.getNodeName().equals("head")){
					accept(e);
				}else if(e.getNodeName().equals("body")){
					HTML5OM.this.body = e;
					inBody = true;
					accept(e);
					inBody = false;
				}else if(e.getNodeName().equals("script")){
					if(!inBody){
						inScript = true;
						accept(e);
						inScript = false;
						if(e.hasAttribute("src"))
							jslibs.add(e);
					}
				}else if(e.getNodeName().equals("style")){
					inStyle = true;
					accept(e);
					inStyle = false;
				}else if(e.getNodeName().equals("link")){
					if(e.hasAttribute("href"))
						csslibs.add(e);
				}else if(e.getNodeName().equals("meta")){
					metamap.put(e.getAttribute("name"), e.getAttribute("content"));
					accept(e);
				}else{
					accept(e);
				}
			}
			
			@Override
			public void visit(Text n) {
				if(inScript){
					findRequires(n.getNodeValue());
					javascripts.add(JSParser.clean(n.getNodeValue()));
				}else if(inStyle){
					try {
						parseStyleSheet(CSS2Parser.clean(n.getNodeValue()));
					} catch (DOMException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		};
		visitor.visit(document);

		HTML5Visitor v = new HTML5Visitor() {
			@Override
			public void visit(Text n) {
				
			}
			
			@Override
			public void visit(Element e) {
			}
		};
		v.visit(document);
	}
	
	public void parse(String name, Reader r) throws SAXException, IOException{
		parse(name, new InputSource(r));
	}

	public String getClassName() {
		return className;
	}

	private void findRequires(String js){
		Pattern pattern = Pattern.compile("require\\s*\\(\\s*[\"'](.*?)[\"']\\s*\\)\\s*;?");
		Matcher matcher = pattern.matcher(js);
		while (matcher.find()) {
			requires.add(matcher.group(1));
		}
	}

	public String getScripts() {
		StringBuffer b = new StringBuffer();
		for (Iterator<String> j = javascripts.iterator(); j.hasNext();) {
			b.append(j.next());
		}
		return b.toString();
	}
	
	public List<CSSStyleSheet> getStyles() {
		return stylesheets;
	}
	
	public List<String> getRequires() {
		return requires;
	}

	public List<Element> getJSLibraries() {
		return jslibs;
	}

	public List<Element> getCSSLibraries() {
		return csslibs;
	}
	
	public Map<String,String> getMETAMap() {
		return metamap;
	}
	
	public void removeProperty(Element e, String prop) {
		List<CSSStyleRule> l = map.get(e);
		if(l != null){
			for (Iterator<CSSStyleRule> iter = l.iterator(); iter.hasNext();) {
				CSSStyleDeclaration style = iter.next().getStyle();
				style.removeProperty(prop);
			}
		}
		
		String attr = e.getAttribute("style");
		if(attr != null){
			try {
				CSSStyleDeclaration style = CSS2Parser.parsestyle(attr);
				CSSValue v = style.getPropertyCSSValue(prop);
				if(v != null){
					style.removeProperty(prop);
					e.setAttribute("style", style.getCssText());
				}
			} catch (IOException e1) {
			}
		}
	}
	
	public String getProperty(Element e, String prop) {
		CSSValue v = getPropertyValue(e, prop);
		return v != null ? v.getCssText() : null;
	}

	public CSSValue getPropertyValue(Element e, String prop) {
		List<CSSStyleRule> l = map.get(e);
		CSSValue value = null;
		CSSValue imporantValue = null;
		if(l != null){
			for (Iterator<CSSStyleRule> iter = l.iterator(); iter.hasNext();) {
				CSSStyleDeclaration style = iter.next().getStyle();
				if(style.getPropertyPriority(prop).equals("important")){
					CSSValue v = style.getPropertyCSSValue(prop);
					if(v != null)
						imporantValue = v;
				}else{
					CSSValue v = style.getPropertyCSSValue(prop);
					if(v != null)
						value = v;
				}
			}
		}
		String attr = e.getAttribute("style");
		if(attr != null){
			try {
				CSSStyleDeclaration style = CSS2Parser.parsestyle(attr);
				if(style.getPropertyPriority(prop).equals("important")){
					CSSValue v = style.getPropertyCSSValue(prop);
					if(v != null)
						imporantValue = v;
				}else{
					CSSValue v = style.getPropertyCSSValue(prop);
					if(v != null)
						value = v;
				}
			} catch (IOException e1) {
			}
		}
		return imporantValue != null ? imporantValue : value;
	}

	public void setProperty(Element e, String prop, String value) {
		String attr = e.getAttribute("style");
		if(attr != null){
			try {
				CSSStyleDeclaration style = CSS2Parser.parsestyle(attr);
				style.setProperty(prop, value, null);
				e.setAttribute("style", style.getCssText());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}else{
			e.setAttribute("style", prop + ":" + value + ";");
		}
	}

	public void renameProperty(Element e, String prop, String newprop) {
		List<CSSStyleRule> l = map.get(e);
		if(l != null){
			for (Iterator<CSSStyleRule> iter = l.iterator(); iter.hasNext();) {
				CSSStyleDeclaration style = iter.next().getStyle();
				CSSValue v = style.getPropertyCSSValue(prop);
				if(v != null){
					style.setProperty(newprop, v.getCssText(), style.getPropertyPriority(prop));
					style.removeProperty(prop);
				}
			}
		}
		
		String attr = e.getAttribute("style");
		if(attr != null){
			try {
				CSSStyleDeclaration style = CSS2Parser.parsestyle(attr);
				CSSValue v = style.getPropertyCSSValue(prop);
				if(v != null){
					style.setProperty(newprop, v.getCssText(), style.getPropertyPriority(prop));
					style.removeProperty(prop);
					e.setAttribute("style", style.getCssText());
				}
			} catch (IOException e1) {
			}
		}
	}
	
	public List<HTML5TagResult> getTagResult(Element e){
		return extmap.get(e);
	}
}

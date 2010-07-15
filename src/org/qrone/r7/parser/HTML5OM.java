package org.qrone.r7.parser;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.qrone.coder.QClass;
import org.qrone.coder.QFunc;
import org.qrone.coder.QState;
import org.qrone.coder.render.QLangJQuery;
import org.qrone.r7.QrONEUtils;
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
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.stylesheets.MediaList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import se.fishtank.css.selectors.NodeSelectorException;
import se.fishtank.css.selectors.dom.DOMNodeSelector;

public class HTML5OM {
	private HTML5Deck deck;
	
	private Document document;
	private Element body;
	private Map<Node, List<CSSStyleRule>> map = new Hashtable<Node, List<CSSStyleRule>>();
	
	private List<CSS3OM> stylesheets = new LinkedList<CSS3OM>();
	private List<String> javascripts = new LinkedList<String>();

	private List<String> requires = new LinkedList<String>();
	private List<Element> jslibs = new LinkedList<Element>();
	private List<Element> csslibs = new LinkedList<Element>();

	private Map<String, String> metamap = new Hashtable<String, String>();
	
	private DOMNodeSelector nodeselector;

	private Map<Element, List<HTML5TagResult>> extmap = new Hashtable<Element, List<HTML5TagResult>>();

	private URI uri;
	
	public HTML5OM(HTML5Deck deck, URI uri){
		this.uri = uri;
		this.deck = deck;
	}
	
	public Set<Node> select(String selector) throws NodeSelectorException{
		return nodeselector.querySelectorAll(selector);
	}
	
	public void parseStyleSheet(URI path, String css) throws IOException{
		CSS3OM s = new CSS3OM();
		s.parse(path, css);
		CSS3Visitor v = new CSS3Visitor(){

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
		v.visit(s.getStyleSheet());
		stylesheets.add(s);
	}
	
	public void parse(URIResolver resolver) throws SAXException, IOException{
		document = HTML5Parser.parse(new InputSource(resolver.getInputStream(uri)));
		nodeselector = new DOMNodeSelector(document);
		HTML5Visitor visitor = new HTML5Visitor() {
			
			private boolean inBody = false;
			private boolean inScript = false;
			private boolean inStyle = false;
			
			@Override
			public void visit(Element e) {
				for (Iterator<HTML5TagHandler> i = deck.getTagHandlers().iterator(); i
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
						parseStyleSheet(null, CSS3Parser.clean(n.getNodeValue()));
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
	
	public URI getURI(){
		return uri;
	}

	private void findRequires(String js){
		Pattern pattern = Pattern.compile("require\\s*\\(\\s*[\"'](.*?)[\"']\\s*\\)\\s*;?");
		Matcher matcher = pattern.matcher(js);
		while (matcher.find()) {
			requires.add(matcher.group(1));
		}
	}

	/*
	public String getScripts() {
		StringBuffer b = new StringBuffer();
		for (Iterator<String> j = javascripts.iterator(); j.hasNext();) {
			b.append(j.next());
		}
		return b.toString();
	}
	*/
	
	public List<CSS3OM> getStyles() {
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
				CSSStyleDeclaration style = CSS3Parser.parsestyle(attr);
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
				CSSStyleDeclaration style = CSS3Parser.parsestyle(attr);
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
				CSSStyleDeclaration style = CSS3Parser.parsestyle(attr);
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
				CSSStyleDeclaration style = CSS3Parser.parsestyle(attr);
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
	
	//---

	public void process(HTML5Writer t){
		process(t, false, null);
	}

	public void process(HTML5Writer t, boolean bodyOnly, String id){
		process(t, bodyOnly, null, null, null);
	}
	
	public void process(final HTML5Writer t, final boolean bodyOnly, 
			final Stack<HTML5OM> xomlist, final String target, String id){
		if(!bodyOnly){
			String path = getMETAMap().get("include-in");
			if(path != null){
				String[] paths = path.split("#", 2);
				if(paths.length == 2){
					HTML5OM xom = deck.compile(getURI().resolve(paths[0]));
					if(xom != null){
						Stack<HTML5OM> xoml = xomlist;
						if(xomlist == null){
							xoml = new Stack<HTML5OM>();
						}
						xoml.push(this);
						xom.process(t, false, xoml, paths[1], null);
						return;
					}
				}
			}
		}
		
		HTML5Selializer s = new HTML5Selializer() {
			int formatting = 0;
			boolean inBody;
			boolean inScript;
			
			@Override
			public void visit(Document e) {
				out("<!DOCTYPE html>");
				super.visit(e);
			}
			
			@Override
			public void visit(Element e) {
				if(e.getNodeName().equals("head")){
					start(e);
					accept(e);
					out(deck.getRecurseHeader(b, getURI(), xomlist));
					end(e);
				}else if(e.getNodeName().equals("body")){
					if(!bodyOnly){
						start(e);
					}
					inBody = true;
					accept(e);
					inBody = false;
					if(!bodyOnly){
						end(e);
					}
				}else if(e.getNodeName().equals("script")){
					if(inBody){
						start(e);
						inScript = true;
						accept(e);
						inScript = false;
						end(e);
					}
				}else if(e.getNodeName().equals("style")){
				}else if(e.getNodeName().equals("link")){
				}else if(e.getNodeName().equals("pre") || e.getNodeName().equals("code")){
					formatting++;
					out(e);
					formatting--;
				}else if(e.getNodeName().equals("meta")){
					if(e.getAttribute("name").equals("extends")){
					}else{
						start(e);
						accept(e);
						end(e);
					}
				}else{
					out(e);
				}
			}

			@Override
			public void visit(Text n) {
				if(inScript){
					out(jsmin(n.getNodeValue(), "qrone[\"" + getURI().toString() + "\"]"));
				}else if(formatting>0){
					writeraw(n.getNodeValue());
				}else if(inBody){
					write(n.getNodeValue());
				}
			}
			
			@Override
			protected void out(Element e) {
				final String include = getProperty(e, "include");
				final String uniqueid = QrONEUtils.uniqueid();
				if(include != null){
					final String path = CSS3Parser.pullstring(include);
					if(path != null && path.trim().length() > 0){
						super.out(e,new Delegate() {
							@Override
							public void accept() {
								HTML5OM xom = deck.compile(getURI().resolve(path));
								xom.process(t, true, uniqueid);
							}
						});
					}else{
						try{
							throw new IOException();
						}catch(IOException e1){
							e1.printStackTrace();
						}
					}
				}else if(target != null && e.getAttribute("id").equals(target)){
					super.out(e,new Delegate() {
						@Override
						public void accept() {
							HTML5OM xom = xomlist.pop();
							xom.process(t, true, uniqueid);
						}
					});
				}else{
					super.out(e);
				}
			}
		};
		s.visit(this, bodyOnly ? body : document.getDocumentElement(), null, t);
	}

	public String getHTML(){
		HTML5Template t = new HTML5Template();
		process(t);
		return t.toString();
	}

	public String getScripts(boolean html){

		final QClass jqueryclass = new QClass(getURI().toString());
		final QFunc method = jqueryclass.constructor();
		method.arg("String", "id");
		final QState jqueryhtml = method.state().returns();
		
		process(new HTML5Writer() {
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
		}, true, null);
		
		StringBuilder b = new StringBuilder();
		if(!html){
			b.append("qrone[\"" + getURI().toString() + "\"]=function(){};");
		}else{
			QLangJQuery q = new QLangJQuery();
			q.accept(jqueryclass);
			b.append(q.build());
		}
		
		for (Iterator<String> i = javascripts.iterator(); i
				.hasNext();) {
			String userjs = i.next();
			b.append(JSParser.compress(userjs.toString(), true)
				.replace("__QRONE_PREFIX_NAME__","qrone[\"" + getURI().toString() + "\"]"));
		}
		
		return b.toString();
	}
	
	public String getScripts() {
		return getScripts(true);
	}
	
	public String serialize(){
		return serialize(null);
	}
	
	public String serialize(String lang){
		if(lang != null && lang.equals("js")){
			return getScripts(true);
		}else{
			return getHTML();
		}
	}
	
}

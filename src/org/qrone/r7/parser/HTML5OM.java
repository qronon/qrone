package org.qrone.r7.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.qrone.coder.QClass;
import org.qrone.coder.QFunc;
import org.qrone.coder.QState;
import org.qrone.coder.render.QLangJQuery;
import org.qrone.r7.parser.HTML5Deck.HTML5Set;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.tag.HTML5TagHandler;
import org.qrone.r7.tag.HTML5TagResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.css.CSSMediaRule;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.stylesheets.MediaList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import se.fishtank.css.selectors.NodeSelectorException;
import se.fishtank.css.selectors.dom.DOMNodeSelector;

public class HTML5OM {
	private HTML5Deck deck;
	private CSS3Deck cssdeck;
	
	private Document document;
	private Element body;
	private Map<Node, List<CSS3Rule>> map = new Hashtable<Node, List<CSS3Rule>>();
	
	private List<CSS3OM> stylesheets = new LinkedList<CSS3OM>();
	private List<String> javascripts = new LinkedList<String>();

	private List<String> requires = new LinkedList<String>();
	private List<Element> jslibs = new LinkedList<Element>();
	private List<Element> csslibs = new LinkedList<Element>();

	private Map<String, String> metamap = new Hashtable<String, String>();
	
	private DOMNodeSelector nodeselector;

	private Map<Element, List<HTML5TagResult>> extmap = new Hashtable<Element, List<HTML5TagResult>>();
	private Map<String, Set<Node>> selectcache = new Hashtable<String, Set<Node>>();
	
	private URI uri;
	
	public HTML5OM(HTML5Deck deck, CSS3Deck cssdeck, URI uri){
		this.uri = uri;
		this.deck = deck;
		this.cssdeck = cssdeck;
	}
	
	public HTML5Deck getDeck(){
		return deck;
	}

	public CSS3Deck getCSS3Deck(){
		return cssdeck;
	}

	public URI getURI(){
		return uri;
	}
	
	public List<CSS3OM> getStyleSheets(){
		return stylesheets;
	}
	
	public Map<Node, List<CSS3Rule>> getCSSRuleMap(){
		return map;
	}
	
	public Document getDocument(){
		return document;
	}

	public Element getBody(){
		return body;
	}
	
	public Set<Node> select(String selector){
		try{
			Set<Node> nodes = selectcache.get(selector);
			if(nodes == null){
				nodes = nodeselector.querySelectorAll(selector);
				selectcache.put(selector, nodes);
			}
			return nodes;
		}catch(NodeSelectorException e){}
		return null;
	}

	
	private Map<String, Map<Object,Set<Node>>> selectorCache 
		= new HashMap<String, Map<Object,Set<Node>>>();
	

	public Set<Node> select(String selector, Object o){
		try{
			Map<Object,Set<Node>> cache = selectorCache.get(selector);
			if(cache != null){
				Set<Node> rset = cache.get(o);
				if(rset != null){
					return rset;
				}
			}else{
				cache = new HashMap<Object, Set<Node>>();
				selectorCache.put(selector, cache);
			}
			
			Set<Node> lhs = null;
			if(o instanceof Element){
				DOMNodeSelector ns = new DOMNodeSelector((Element)o);
				lhs = ns.querySelectorAll(selector);
			}else if(o instanceof Set){
				DOMNodeSelector ns;
				lhs = new LinkedHashSet<Node>();
				Set<Node> l = (Set<Node>)o;
				for (Iterator<Node> i = l.iterator(); i.hasNext();) {
					Node n = i.next();
					ns = new DOMNodeSelector(n);
					lhs.addAll(ns.querySelectorAll(selector));
				}
			}
			
			cache.put(o, lhs);
			return lhs;
			
		} catch (NodeSelectorException e) {}
		return null;
	}
	
	public void parse(final URIResolver resolver) throws SAXException, IOException{
		InputStream in = resolver.getInputStream(uri);
		try{
			document = HTML5Parser.parse(new InputSource(in));
		}finally{
			in.close();
		}
		
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
					HTML5TagResult r = h.process(new HTML5Element(HTML5OM.this, null, e));
					if(r != null){
						List<HTML5TagResult> l = extmap.get(e);
						if(l == null){
							l = new ArrayList<HTML5TagResult>();
							extmap.put(e, l);
						}
						l.add(r);
					}
				}
				
				NamedNodeMap map = e.getAttributes();
				for (int i = 0; i < map.getLength(); i++) {
					Node n = map.item(i);
					if(n.getNodeName().startsWith("on")){
						e.setAttribute(n.getNodeName(), JSParser.compress(n.getNodeValue(),true)
								.replace("__QRONE_PREFIX_NAME__", 
								"qrone('" + getURI().toString() + "','__QRONE_ID__')"));
					}else if(n.getNodeName().equals("href") && n.getNodeValue().startsWith("javascript:")){
						String js = n.getNodeValue();
						if(js.startsWith("javascript:")){
							js = js.substring("javascript:".length());
							e.setAttribute(n.getNodeName(), "javascript:" + JSParser.compress(js,true)
									.replace("__QRONE_PREFIX_NAME__", 
									  "qrone('" + getURI().toString() + "','__QRONE_ID__')"));
						}
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
					if(e.hasAttribute("href")){
						if(e.hasAttribute("include")){
							try {
								URI cssuri = uri.resolve(new URI(e.getAttribute("href")));
								CSS3OM cssom = cssdeck.compile(cssuri);
								if(cssom != null){
									parseStyleSheet(cssom);
								}
							} catch (URISyntaxException e1) {
								csslibs.add(e);
							} catch (IOException e1) {
								csslibs.add(e);
							}
						}else{
							csslibs.add(e);
						}
					}
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
					CSS3OM cssom;
					try {
						cssom = cssdeck.compile(getURI(), CSS3Parser.clean(n.getNodeValue()));
						parseStyleSheet(cssom);
					} catch (Exception e) {}
				}
			}
			
		};
		visitor.visit(document);
	}

	private void findRequires(String js){
		Pattern pattern = Pattern.compile("require\\s*\\(\\s*[\"'](.*?)[\"']\\s*\\)\\s*;?");
		Matcher matcher = pattern.matcher(js);
		while (matcher.find()) {
			requires.add(matcher.group(1));
		}
	}
	
	
	public List<HTML5TagResult> getTagResult(Element e){
		return extmap.get(e);
	}
	
	public void process(final HTML5Writer w, final HTML5Template t,
			final Node node, String id, final Set<HTML5OM> xomlist, final String ticket){
		if(xomlist != null && !xomlist.contains(this)){
			xomlist.add(this);
		}
		
		final HTML5Set set;
		if(node == document){
			set = getRecurseHeader(getURI(), xomlist, ticket);
		}else{
			set = null;
		}
		
		HTML5TagWriter s = new HTML5Selializer(body,set,deck,node,uri,w,t,this, id, ticket);
		s.visit(node);
	}
	
	private void parseStyleSheet(final CSS3OM cssom) throws IOException{
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
				Set<Node> set = select(style.getSelectorText());
				if(set != null){
					for (Iterator<Node> i = set.iterator(); i
							.hasNext();) {
						Node node = i.next();
						List<CSS3Rule> list = map.get(node);
						if(list == null){
							list = new ArrayList<CSS3Rule>();
							map.put(node, list);
						}
						list.add(new CSS3Rule(cssom, style));
					}
				}
			}
		};
		v.visit(cssom.getStyleSheet());
		stylesheets.add(cssom);
	}

	private String scriptCache = null;
	private String scriptCacheNoHTML = null;
	private String getScripts(boolean html, String ticket){
		if(html){
			if(scriptCache != null){
				return scriptCache;
			}
		}else{
			if(scriptCacheNoHTML != null){
				return scriptCacheNoHTML;
			}
		}

		final QClass jqueryclass = new QClass(getURI().toString());
		final QFunc method = jqueryclass.constructor();
		method.arg("String", "id");
		final QState jqueryhtml = method.state().returns();
		
		final HTML5Writer t = new HTML5Writer() {
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
		};
		process(t, null, body, null, null, ticket);
		
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
		

		if(html){
			scriptCache = b.toString();
		}else{
			scriptCacheNoHTML = b.toString();
		}
		return b.toString();
	}
	
	private HTML5Set getRecurseHeader(URI file, Set<HTML5OM> xomlist, String ticket){
		return getRecursive(file, xomlist, ticket);
	}
	
	private HTML5Set getRecursive(URI file, Set<HTML5OM> xomlist, String ticket){
		HTML5Set set = new HTML5Set();
		Set<URI> s = new HashSet<URI>();
		getRecursive(file, set.js, set.css, set.jslibs, set.csslibs, s, true,ticket);
		if(xomlist != null){
			for (Iterator<HTML5OM> i = xomlist.iterator(); i.hasNext();) {
				HTML5OM xom = i.next();
				getRecursive(xom.getURI(), set.js, set.css, set.jslibs, set.csslibs, s, true,ticket);
			}
		}
		return set;
	}

	private void getRecursive(URI file, 
			StringBuffer js, List<CSS3OM> css, 
			List<Element> jslibs, List<Element> csslibs, 
			Set<URI> clses, boolean first, String ticket){
		if(file == null || clses.contains(file)) return;
		clses.add(file);
		
		HTML5OM c = deck.compile(file);
		if(c != null){
			String extend = metamap.get("extends");
			if(extend != null)
				getRecursive(file.resolve(extend), js, css, jslibs, csslibs, clses, false,ticket);
			
			if(first)
				js.append("if(!window.qrone)window.qrone=function(){};");
			js.append(c.getScripts(!first,ticket));
			css.addAll(stylesheets);
			
			jslibs.addAll(this.jslibs);
			csslibs.addAll(this.csslibs);
	
			for (Iterator<String> iter = requires.iterator(); iter.hasNext();) {
				getRecursive(file.resolve(iter.next()), js, css, jslibs, csslibs, clses, false,ticket);
			}
		}
	}
}

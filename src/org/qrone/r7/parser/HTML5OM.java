package org.qrone.r7.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
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
import org.w3c.dom.DOMException;
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
	
	private List<CSS3OM> stylesheets = new LinkedList<CSS3OM>();
	private List<String> javascripts = new LinkedList<String>();

	private List<String> jslibs = new LinkedList<String>();
	private List<Element> csslibs = new LinkedList<Element>();

	private boolean hasQroneObjectInJS = false;
	
	private Map<String, String> metamap = new Hashtable<String, String>();
	
	private DOMNodeSelector nodeselector;

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
				
				NamedNodeMap map = e.getAttributes();
				for (int i = 0; i < map.getLength(); i++) {
					Node n = map.item(i);
					if(n.getNodeName().startsWith("on")){
						e.setAttribute(n.getNodeName(), JSParser.compress(n.getNodeValue(),"__QRONE_ID__"));
					}else if(n.getNodeName().equals("href") && n.getNodeValue().startsWith("javascript:")){
						String js = n.getNodeValue();
						if(js.startsWith("javascript:")){
							js = js.substring("javascript:".length());
							e.setAttribute(n.getNodeName(), "javascript:" + JSParser.compress(js,"__QRONE_ID__"));
						}
					}else if(n.getNodeName().equals("style")){
						String css = n.getNodeValue();
						try {
							e.setAttribute("style", new CSS3Serializer().append(CSS3Parser.parsestyle(css)).toString());
						} catch (DOMException e1) {
						} catch (IOException e1) {
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
							jslibs.add(e.getAttribute("src"));
					}
				}else if(e.getNodeName().equals("style")){
					inStyle = true;
					accept(e);
					inStyle = false;
				}else if(e.getNodeName().equals("link")){
					if(isCSSLinkTab(e)){
						csslibs.add(e);
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
					String js = n.getNodeValue();
					String cjs = JSParser.compress(JSParser.clean(js),"__QRONE_ID__");
					if(cjs.indexOf("__QRONE_ID__")>=0){
						hasQroneObjectInJS = true;
					}
					javascripts.add(cjs);
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
	
	public static boolean isCSSLinkTab(Element e){
		String href = e.getAttribute("href");
		if(href != null){
			if(href.toLowerCase().endsWith(".css")){
				return true;
			}
			
			String rel = e.getAttribute("rel");
			if(rel != null && rel.toLowerCase().equals("stylesheet")){
				return true;
			}

			String type = e.getAttribute("type");
			if(type != null && type.toLowerCase().equals("text/css")){
				return true;
			}
			
		}
		return false;
	}
	
	public void process(final HTML5Writer w, final HTML5Template t,
			final Node node, String id, final Set<HTML5OM> xomlist, final String ticket){
		if(xomlist != null && !xomlist.contains(this)){
			xomlist.add(this);
		}
		
		final HTML5Set set;
		if(node == document){
			set = getRecursive(id, xomlist, ticket);
		}else{
			set = null;
		}
		
		HTML5TagWriter s = new HTML5Selializer(body,set,deck,node,uri,w,t,this, id, ticket);
		s.visit(node);
	}
	
	private void parseStyleSheet(final CSS3OM cssom) throws IOException{
		stylesheets.add(cssom);
	}

	private String scriptCache = null;
	public String getScripts(boolean inline){
		if(scriptCache != null){
				return scriptCache;
		}
		
		StringBuilder b = new StringBuilder();
		if(inline){
			b.append("qrone[\"" + getURI().toString() + "\"]=function(){};");
		}
		
		for (Iterator<String> i = javascripts.iterator(); i
				.hasNext();) {
			b.append(i.next().replaceAll("__QRONE_ID__", "qrone[\"" + getURI().toString() + "\"]"));
		}
		
		scriptCache = b.toString();
		return b.toString();
	}
	
	private HTML5Set getRecursive(String id, Set<HTML5OM> xomlist, String ticket){
		HTML5Set set = new HTML5Set();
		if(xomlist != null){
			for (Iterator<HTML5OM> i = xomlist.iterator(); i.hasNext();) {
				if(i.next().hasQroneObjectInJS){
					set.js.append("if(!window.qrone)window.qrone=function(){};");
					jslibs.add("/system/resource/qrone.js");
					break;
				}
			}
			
			for (Iterator<HTML5OM> i = xomlist.iterator(); i.hasNext();) {
				HTML5OM xom = i.next();
				set.js.append(xom.getScripts(xom.hasQroneObjectInJS));
				set.css.addAll(xom.stylesheets);
				
				set.jslibs.addAll(xom.jslibs);
				set.csslibs.addAll(xom.csslibs);
			}
		}
		return set;
	}
}

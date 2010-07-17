package org.qrone.r7.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.qrone.r7.QrONEUtils;
import org.qrone.r7.handler.HTML5TagHandler;
import org.w3c.dom.Element;
import org.w3c.dom.css.CSSRuleList;
import org.xml.sax.SAXException;

public class HTML5Deck {
	private URIResolver resolver;
	private ImageSpriter spriter;
	
	private Map<URI, HTML5OM> map = new Hashtable<URI, HTML5OM>();
	private List<HTML5TagHandler> handlers = new ArrayList<HTML5TagHandler>();
	
    
    public HTML5Deck(URIResolver resolver){
    	this.resolver = resolver;
    	spriter = new ImageSpriter(resolver);
    }
    
    public URIResolver getResolver(){
    	return resolver;
    }
    
    public ImageSpriter getSpriter(){
    	return spriter;
    }
    
	public void addTagHandler(HTML5TagHandler h){
		handlers.add(h);
	}
	
	public List<HTML5TagHandler> getTagHandlers() {
		return handlers;
	}
    
	public HTML5OM compile(URI f){
		HTML5OM o = map.get(f);
		try {
			if(o == null){
				HTML5OM xom = new HTML5OM(this, f);
				map.put(f, xom);
				xom.parse(resolver);
				return xom;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return o;
	}
	
	public HTML5Set getRecursive(URI file, Set<HTML5OM> xomlist){
		HTML5Set set = new HTML5Set();
		Set<URI> s = new HashSet<URI>();
		getRecursive(file, set.js, set.css, set.jslibs, set.csslibs, s, true);
		if(xomlist != null){
			for (Iterator<HTML5OM> i = xomlist.iterator(); i.hasNext();) {
				HTML5OM xom = i.next();
				getRecursive(xom.getURI(), set.js, set.css, set.jslibs, set.csslibs, s, true);
			}
		}
		return set;
	}

	public void getRecursive(URI file, 
			StringBuffer js, List<CSS3OM> css, 
			List<Element> jslibs, List<Element> csslibs, 
			Set<URI> clses, boolean first){
		if(file == null || clses.contains(file)) return;
		clses.add(file);
		
		HTML5OM c = compile(file);
		if(c != null){
			String extend = c.getMETAMap().get("extends");
			if(extend != null)
				getRecursive(file.resolve(extend), js, css, jslibs, csslibs, clses, false);
			
			js.append(c.getScripts(!first));
			css.addAll(c.getStyles());
			
			jslibs.addAll(c.getJSLibraries());
			csslibs.addAll(c.getCSSLibraries());
	
			for (Iterator<String> iter = c.getRequires().iterator(); iter.hasNext();) {
				getRecursive(file.resolve(iter.next()), js, css, jslibs, csslibs, clses, false);
			}
		}
	}
	
	public String getRecurseHeader(HTML5Writer b, URI file, Set<HTML5OM> xomlist){
		HTML5Set set = getRecursive(file, xomlist);
		//StringBuffer b = new StringBuffer();
		
		//---------------
		// script src
		//---------------
		Set<String> hash = new HashSet<String>();
		for (Iterator<Element> i = set.jslibs.iterator(); i.hasNext();) {
			Element el = i.next();
			if(!hash.contains(el.getAttribute("src"))){
				hash.add(el.getAttribute("src"));
				if(!el.hasAttribute("inline")){
					b.append("<script src=\"");
					b.append(QrONEUtils.escape(el.getAttribute("src")));
					b.append("\"></script>");
				}
			}
		}

		//---------------
		// script inline
		//---------------
		StringBuffer js = new StringBuffer();
		hash = new HashSet<String>();
		for (Iterator<Element> i = set.jslibs.iterator(); i.hasNext();) {
			Element el = i.next();
			if(!hash.contains(el.getAttribute("src"))){
				hash.add(el.getAttribute("src"));
				if(el.hasAttribute("inline")){
					try {
						js.append(QrONEUtils.convertStreamToString(
								resolver.getInputStream(
										file.resolve(el.getAttribute("src")))));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}

		//---------------
		// css href
		//---------------
		js.append(set.js.toString());
		String js2 = JSParser.compress(js.toString());

		if(js2.length() > 0){
			b.append("<script>");
			b.append(js2);
			b.append("</script>");
		}
		
		for (Iterator<Element> i = set.csslibs.iterator(); i.hasNext();) {
			Element el = i.next();
			b.append("<link href=\"");
			b.append(QrONEUtils.escape(el.getAttribute("src")));
			b.append("\" />");
		}

		//---------------
		// css inline
		//---------------
		StringBuffer css = new StringBuffer();
		for (Iterator<CSS3OM> j = set.css.iterator(); j.hasNext();) {
			CSSRuleList l = j.next().getStyleSheet().getCssRules();
			for (int i = 0; i < l.getLength(); i++) {
				css.append(l.item(i).getCssText());
			}
		}
		
		String css2 = CSS3Parser.compress(css.toString());
		if(css2.length() > 0){
			b.append("<style>");
			b.append(css2);
			b.append("</style>");
		}
		
		return b.toString();
	}
	
	public static class HTML5Set{
		public StringBuffer js = new StringBuffer();
		public List<CSS3OM> css = new ArrayList<CSS3OM>();
		public List<Element> jslibs = new ArrayList<Element>();
		public List<Element> csslibs = new ArrayList<Element>();
	}
}

package org.qrone.r7.parser;

import java.io.File;
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

import org.qrone.img.ImageBufferService;
import org.qrone.r7.QrONEUtils;
import org.qrone.r7.resolver.FileResolver;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.tag.HTML5TagHandler;
import org.w3c.dom.Element;
import org.w3c.dom.css.CSSRuleList;
import org.xml.sax.SAXException;

public class HTML5Deck {
	private URIResolver resolver;
	private ImageSpriter spriter;
	
	private Map<URI, HTML5OM> map = new Hashtable<URI, HTML5OM>();
	private List<HTML5TagHandler> handlers = new ArrayList<HTML5TagHandler>();
	
	public HTML5Deck(File file, ImageBufferService service){
		this(new FileResolver(file), service);
	}
    
    public HTML5Deck(URIResolver resolver, ImageBufferService service){
    	this.resolver = resolver;
    	spriter = new ImageSpriter(resolver, service);
    }
    
    public void update(URI uri){
    	try {
			spriter.update(uri);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			if(o == null || resolver.updated(f)){
				HTML5OM xom = new HTML5OM(this, f);
				map.put(f, xom);
				xom.parse(resolver);
				return xom;
			}
		} catch (FileNotFoundException e) {
		} catch (SAXException e) {
		} catch (IOException e) {
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
	
	private Map<String, String> inlineJSMap = new Hashtable<String, String>();

	public void outputScripts(HTML5Writer b, HTML5Set set, URI file){

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
					String key = el.getAttribute("src");
					String ijsc = inlineJSMap.get(key);
					if(ijsc == null){
						try {
							String ijs = JSParser.compress(
									QrONEUtils.convertStreamToString(
											resolver.getInputStream(
													file.resolve(key))));
							inlineJSMap.put(key, ijs);
							js.append(ijs);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}else{
						js.append(ijsc);
					}
				}
			}
		}
		
		js.append(set.js.toString());
		if(js.length() > 0){
			b.append("<script>");
			b.append(js.toString());
			b.append("</script>");
		}
	}
	public void outputStyles(HTML5Writer b, HTML5Set set, URI file){

		//---------------
		// css href
		//---------------
		
		for (Iterator<Element> i = set.csslibs.iterator(); i.hasNext();) {
			Element el = i.next();
			b.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
			b.append(QrONEUtils.escape(el.getAttribute("href")));
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
	}
	
	public HTML5Set getRecurseHeader(URI file, Set<HTML5OM> xomlist){
		return getRecursive(file, xomlist);
	}
	
	public static class HTML5Set{
		public StringBuffer js = new StringBuffer();
		public List<CSS3OM> css = new ArrayList<CSS3OM>();
		public List<Element> jslibs = new ArrayList<Element>();
		public List<Element> csslibs = new ArrayList<Element>();
	}
}

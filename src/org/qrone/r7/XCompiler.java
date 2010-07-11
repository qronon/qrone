package org.qrone.r7;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.qrone.r7.parser.CSS2Parser;
import org.qrone.r7.parser.JSParser;
import org.w3c.dom.Element;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;
import org.xml.sax.SAXException;

import fmpp.util.FileUtil;

public class XCompiler {
	public static File root;
	private static Map<File, XOM> map = new Hashtable<File, XOM>();
	
    public static void main(String[] args) {
    	String[] a = {"-v", "site/test"};
    	QrONECompressor.main(a);
	}
    
	public static XOM compile(File f){
		XOM o = map.get(f);
		try {
			if(o == null){
				XOM xom = new XOM();
				map.put(f, xom);
				xom.parse(f);
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
	
	public static HTML5Set getRecursive(File file){
		HTML5Set set = new HTML5Set();
		getRecursive(file, set.js, set.css, set.jslibs, set.csslibs, new HashSet<File>(), true);
		return set;
	}

	public static void getRecursive(File file, 
			StringBuffer js, List<CSSStyleSheet> css, 
			List<Element> jslibs, List<Element> csslibs, 
			Set<File> clses, boolean first){
		if(file == null || clses.contains(file)) return;
		clses.add(file);
		
		XOM c = compile(file);
		if(c != null){
			String extend = c.getMETAMap().get("extends");
			if(extend != null)
				getRecursive(getFileByName(file.getParentFile(), extend), js, css, jslibs, csslibs, clses, false);
			
			js.append(c.getScripts(!first));
			css.addAll(c.getStyles());
			
			jslibs.addAll(c.getJSLibraries());
			csslibs.addAll(c.getCSSLibraries());
	
			for (Iterator<String> iter = c.getRequires().iterator(); iter.hasNext();) {
				getRecursive(getFileByName(file.getParentFile(), iter.next()), js, css, jslibs, csslibs, clses, false);
			}
		}
	}
	
	public static String getRecurseHeader(File file){
		HTML5Set set = XCompiler.getRecursive(file);
		StringBuffer b = new StringBuffer();
		
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
						js.append(QrONEUtils.getContent(file.getParentFile(), el.getAttribute("src")));
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
		css.append(set.css.toString());
		for (Iterator<CSSStyleSheet> j = set.css.iterator(); j.hasNext();) {
			CSSRuleList l = j.next().getCssRules();
			for (int i = 0; i < l.getLength(); i++) {
				css.append(l.item(i).getCssText());
			}
		}
		
		String css2 = CSS2Parser.compress(css.toString());
		if(css2.length() > 0){
			b.append("<style>");
			b.append(css2);
			b.append("</style>");
		}
		
		return b.toString();
	}
	
	public static class HTML5Set{
		public StringBuffer js = new StringBuffer();
		public List<CSSStyleSheet> css = new ArrayList<CSSStyleSheet>();
		public List<Element> jslibs = new ArrayList<Element>();
		public List<Element> csslibs = new ArrayList<Element>();
	}

	private static File getFileByName(File file, String name){
		try {
			return FileUtil.resolveRelativeUnixPath(root, file, name);
		} catch (IOException e) {
		}
		return null;
	}
}

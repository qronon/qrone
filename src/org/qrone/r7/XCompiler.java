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
import java.util.Stack;

import org.qrone.r7.parser.CSS3OM;
import org.qrone.r7.parser.CSS3Parser;
import org.qrone.r7.parser.HTML5Writer;
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

	public static XOM compile(File file, String path){
		try {
			return compile(
					FileUtil.resolveRelativeUnixPath(root, file.getParentFile(), path)
					);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
	
	public static HTML5Set getRecursive(File file, Stack<XOM> xomlist){
		HTML5Set set = new HTML5Set();
		Set<File> s = new HashSet<File>();
		getRecursive(file, set.js, set.css, set.jslibs, set.csslibs, s, true);
		if(xomlist != null){
			for (Iterator<XOM> i = xomlist.iterator(); i.hasNext();) {
				XOM xom = i.next();
				getRecursive(xom.getFile(), set.js, set.css, set.jslibs, set.csslibs, s, true);
			}
		}
		return set;
	}

	public static void getRecursive(File file, 
			StringBuffer js, List<CSS3OM> css, 
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
	
	public static String getRecurseHeader(HTML5Writer b, File file, Stack<XOM> xomlist){
		HTML5Set set = XCompiler.getRecursive(file, xomlist);
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

	private static File getFileByName(File file, String name){
		try {
			return FileUtil.resolveRelativeUnixPath(root, file, name);
		} catch (IOException e) {
		}
		return null;
	}
}

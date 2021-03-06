package org.qrone.r7.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.WeakHashMap;

import org.mozilla.javascript.EvaluatorException;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleSheet;

import com.steadystate.css.parser.CSSOMParser;
import com.yahoo.platform.yui.compressor.CssCompressor;

public class CSS3Parser{
	//public static Pattern strRegex = Pattern.compile("[\"'](.*?)[\"']");
	//public static Pattern urlRegex = Pattern.compile("url\\s*\\(\\s*[\"']?(.*?)[\"']?\\s*\\)");
	//public static Pattern numberRegex = Pattern.compile("([0-9]+)px");
	//public static Pattern colorRegex = Pattern.compile("(#[a-fA-F0-9]+|rgb\\s*\\(\\s*[^()]+\\s*\\))");

	/*
	public static String pullstring(String style){
		Matcher mm = strRegex.matcher(style);
		if(mm.find()){
			return mm.group(1);
		}else{
			return null;
		}
	}*/
	
	public static String clean(String js){
		return js
			.replaceAll("^<!\\[CDATA\\[", "")
			.replaceAll("\\]\\]>$", "")
			.replaceAll("^<!--", "")
			.replaceAll("-->$", "");
	}

	public static CSSStyleDeclaration parsestyle(String str) throws IOException{
		CSSOMParser parser = new CSSOMParser();
		return parser.parseStyleDeclaration(
				new InputSource(new StringReader(str)));
	}
	
	public static CSSStyleSheet parse(InputSource source) throws IOException{
		CSSOMParser parser = new CSSOMParser();
		return parser.parseStyleSheet(source, null, null);
	}

	public static CSSStyleSheet parse(Reader r) throws IOException{
		return parse(new InputSource(r));
	}

	public static CSSStyleSheet parse(File f) throws IOException{
		return parse(new InputSource(new FileReader(f)));
	}
	
	public static CSSStyleSheet parse(String r) throws IOException{
		return parse(new InputSource(new StringReader(r)));
	}
	
	public static void compress(Reader r, Writer w) 
			throws EvaluatorException, IOException{
		CssCompressor jsc = new CssCompressor(r);
		jsc.compress(w, -1);
	}
	
	public static String compress(Reader r) 
			throws EvaluatorException, IOException{
		StringWriter w = new StringWriter();
		compress(r, w);
		return w.toString();
	}
	
	public static void compress(String r, Writer w) 
			throws EvaluatorException, IOException{
		compress(new StringReader(r), w);
	}

	private static Map<String, String> compressionCache = new WeakHashMap<String, String>();
	public static String compress(String r){
		String c = compressionCache.get(r);
		if(c != null){
			return c;
		}
		
		try {
			StringWriter w = new StringWriter();
			compress(new StringReader(r), w);
			compressionCache.put(r, w.toString());
			return w.toString();
		} catch (EvaluatorException e) {
		} catch (IOException e) {
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
		compressionCache.put(r, r);
		return r;
	}
	
	private static void serialize(Writer w, CSSStyleSheet stylesheet) throws IOException{
		w.append(new CSS3Serializer().append(stylesheet).toString());
	}

	private static String serialize(CSSStyleSheet stylesheet) throws IOException{
		return new CSS3Serializer().append(stylesheet).toString();
	}
}

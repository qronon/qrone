package org.qrone.r7.parser;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.ref.SoftReference;

import org.qrone.util.QrONEUtils;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;

public class CSS3OM{

	public static Pattern urlRegex = Pattern.compile("(url\\s*\\(\\s*[\"']?)(.*?)([\"']?\\s*\\))");
	
	private URI path;
	private CSSStyleSheet stylesheet;
	private SoftReference<String> cache;
	
	public void parse(URI path, String css) throws IOException{
		this.path = path;
		parse(path, new InputSource(new StringReader(css)));
	}
	
	public URI getURI(){
		return path;
	}
	
	public void parse(URI path, InputSource source) throws IOException{
		this.path = path;
		stylesheet = CSS3Parser.parse(source);
	}
	
	public String getCssText(){
		if(cache != null){
			String c = cache.get();
			if(c != null)
				return c;
		}
		
		StringBuffer css = new StringBuffer();
		CSSRuleList l = getStyleSheet().getCssRules();
		for (int i = 0; i < l.getLength(); i++) {
			css.append(l.item(i).getCssText());
		}
		cache = new SoftReference(css.toString());
		return css.toString();
	}
	
	public CSSStyleSheet getStyleSheet(){
		return stylesheet;
	}
	
	public String serialize(){
		StringBuffer css = new StringBuffer();
		CSSRuleList l = getStyleSheet().getCssRules();
		for (int i = 0; i < l.getLength(); i++) {
			css.append(l.item(i).getCssText());
		}

		if(path != null){
			StringBuffer c = new StringBuffer();
			Matcher m = urlRegex.matcher(css.toString());
			
			while(m.find()){
				try {
					URI uri = new URI(m.group(2));
					m.appendReplacement(c, m.group(1) + QrONEUtils.relativize(path,uri) + m.group(3));
				} catch (URISyntaxException e) {}
			}
			
			return c.toString();
		}
		return css.toString();
	}
}

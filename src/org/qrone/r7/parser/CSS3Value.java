package org.qrone.r7.parser;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;

public class CSS3Value {
	public static Pattern urlRegex = Pattern.compile("url\\s*\\(\\s*[\"']?(.*?)[\"']?\\s*\\)");
	
	private URI uri;
	private CSSStyleDeclaration style;
	private String prop;
	private CSSValue v;
	public CSS3Value(URI uri, CSSStyleDeclaration style, CSSValue v, String prop) {
		this.uri = uri;
		this.style = style;
		this.v = v;
		this.prop = prop;
	}

	public String getValue(){
		return v != null ? v.getCssText() : null;
	}
	
	public boolean isImportant(){
		return style.getPropertyPriority(prop).equals("important");
	}
	
	@Override
	public String toString() {
		return getValue();
	}

	public String getURL() {
		return uri.resolve(pullurl(getValue())).toString();
	}
	
	
	private static String pullurl(String style){
		Matcher mm = urlRegex.matcher(style);
		if(mm.find()){
			return mm.group(1);
		}else{
			return null;
		}
	}
}

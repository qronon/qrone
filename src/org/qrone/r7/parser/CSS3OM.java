package org.qrone.r7.parser;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;

public class CSS3OM {
	private String path;
	private CSSStyleSheet stylesheet;
	
	public void parse(String path, String css) throws IOException{
		parse(path, new InputSource(new StringReader(css)));
	}
	
	public void parse(String path, InputSource source) throws IOException{
		this.path = path;
		stylesheet = CSS3Parser.parse(source);
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
		return css.toString();
	}
}

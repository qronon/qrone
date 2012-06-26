package org.qrone.r7.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.css.CSSCharsetRule;
import org.w3c.dom.css.CSSFontFaceRule;
import org.w3c.dom.css.CSSMediaRule;
import org.w3c.dom.css.CSSPageRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSUnknownRule;
import org.w3c.dom.stylesheets.MediaList;

public class CSS3Serializer {
	private StringBuilder str = new StringBuilder();
	
	public CSS3Serializer(){
		
	}
	
	public String toString(){
		return str.toString();
	}
	
	public void append(CSSStyleSheet ss){
		append(ss.getCssRules());
	}

	public void append(CSSRuleList list){
		for (int i = 0; i < list.getLength(); i++) {
			dispatch(list.item(i));
		}
	}
	
	public void dispatch(CSSRule rule){
		if(rule instanceof CSSCharsetRule){
			append((CSSCharsetRule)rule);
		}else if(rule instanceof CSSFontFaceRule){
			append((CSSFontFaceRule)rule);
		}else if(rule instanceof CSSMediaRule){
			append((CSSMediaRule)rule);
		}else if(rule instanceof CSSPageRule){
			append((CSSPageRule)rule);
		}else if(rule instanceof CSSStyleRule){
			append((CSSStyleRule)rule);
		}else if(rule instanceof CSSUnknownRule){
			append((CSSUnknownRule)rule);
		}
	}

	public void append(String s){
		str.append(s);
	}
	
	public void append(CSSCharsetRule rule){
		append("@charset \"" + rule.getEncoding() + "\";");
	}
	
	public void append(CSSFontFaceRule rule){
		append("@font-face{");
		append(rule.getStyle());
		append("}");
	}
	
	public void append(CSSMediaRule rule){
		MediaList ml = rule.getMedia();
		append("@media " + ml.getMediaText() + "{");
		append(rule.getCssRules());
		append("}");
	}
	
	public void append(CSSPageRule rule) {
		append("@page " + rule.getSelectorText() + "{");
		append(rule.getStyle());
		append("}");
		
	}
	
	public void append(CSSStyleRule rule) {
		append(rule.getSelectorText() + "{");
		append(rule.getStyle());
		append("}");
	}
	
	public void append(CSSUnknownRule rule) {
		append(rule.getCssText());
	}
	
	public void append(CSSStyleDeclaration styles){
		boolean ispie = false;
		for (int i = 0; i < styles.getLength(); i++) {
			String property = styles.item(i);
			boolean r = append(property, styles.getPropertyValue(property));
			if(r){
				ispie = true;
			}
			String priority = styles.getPropertyPriority(property);
			
			if(priority == null || priority.equals("")){
				append("!" + priority);
			}
			
			append(";");
		}
		
		if(ispie){
			append("behavior: url(/PIE.htc);");
		}
	}
	
	private Pattern lg = Pattern.compile("linear\\-gradient\\s*\\(([^\\),]+),([^\\),]+)\\)");
	
	public boolean append(String property, String value){
		if(property.equals("border-radius")){
			append("border-radius:" + value + ";");
			append("-webkit-border-radius:" + value + ";");
			append("-moz-border-radius:" + value + ";");
			return true;
		}else if(property.equals("box-shadow")){
			append("box-shadow:" + value + ";");
			append("-webkit-box-shadow:" + value + ";");
			append("-moz-box-shadow:" + value + ";");
			return true;
		}else if(property.equals("border-image")){
			append("border-image:" + value + ";");
			append("-webkit-border-image:" + value + ";");
			append("-moz-border-image:" + value + ";");
			return true;
		}else if(property.equals("background")){
			
			Matcher m = lg.matcher(value);
			if(m.find()){
				String start = m.group(1);
				String end = m.group(2);
				
				append(property + ":" + value);
				append("background:" + start + ";");
				append("background: -webkit-gradient(linear, 0 0, 0 bottom, from(" + start + "), to(" + end + "));");
				append("background: -webkit-linear-gradient(" + start + ", " + end + ");");
				append("background: -moz-linear-gradient(" + start + ", " + end + ");");
				append("background: -ms-linear-gradient(" + start + ", " + end + ");");
				append("background: -o-linear-gradient(" + start + ", " + end + ");");
				append("background: linear-gradient(" + start + ", " + end + ");");
				append("-pie-background: linear-gradient(" + start + ", " + end + ");");
				return true;
			}else{
				append(property + ":" + value);
			}

		}else{
			append(property + ":" + value);
		}
		
		return false;
	}
	
}

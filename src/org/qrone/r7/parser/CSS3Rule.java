package org.qrone.r7.parser;

import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSValue;


public class CSS3Rule {
	private CSS3OM om;
	private CSSStyleRule rule;
	private CSSStyleDeclaration style;
	public CSS3Rule(CSS3OM om, CSSStyleRule rule) {
		this.om = om;
		this.rule = rule;
		this.style = rule.getStyle();
	}
	
	public void setProperty( String newprop, String v, boolean important ) {
		style.setProperty(newprop, v, important ? "important"  : "");
	}
	
	public void removeProperty( String propertyName ) {
		style.removeProperty(propertyName);
	}
	
	public CSS3Value getProperty( String propertyName ){
		CSSValue v = style.getPropertyCSSValue(propertyName);
		if(v != null)
			return new CSS3Value(om.getURI(), style, v, propertyName);
		return null;
	}
}

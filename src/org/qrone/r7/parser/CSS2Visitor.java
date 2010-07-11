package org.qrone.r7.parser;

import org.w3c.dom.css.CSSCharsetRule;
import org.w3c.dom.css.CSSFontFaceRule;
import org.w3c.dom.css.CSSImportRule;
import org.w3c.dom.css.CSSMediaRule;
import org.w3c.dom.css.CSSPageRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

public abstract class CSS2Visitor {

	public void visit(CSSMediaRule rule){
		
	}
	
	public void visit(CSSImportRule rule){
		
	}
	
	public void visit(CSSFontFaceRule rule){
		
	}
	
	public void visit(CSSCharsetRule rule){
		
	}
	
	public void visit(CSSPageRule rule){
		
	}

	public void visit(CSSStyleSheet rule){
		accept(rule);
	}
	
	public abstract void visit(CSSStyleRule rule);
	
	public void accept(CSSMediaRule r){
		CSSRuleList l = r.getCssRules();
		for (int i = 0; i < l.getLength(); i++) {
			dispatch(l.item(i));
		}
	}

	public void accept(CSSImportRule r){
	}

	public void accept(CSSFontFaceRule r){
	}

	public void accept(CSSCharsetRule r){
	}

	public void accept(CSSPageRule r){
	}

	public void accept(CSSStyleRule r){
	}
	
	public void accept(CSSStyleSheet r){
		CSSRuleList l = r.getCssRules();
		for (int i = 0; i < l.getLength(); i++) {
			dispatch(l.item(i));
		}
	}
	
	public void dispatch(CSSRule n){
		if(n instanceof CSSStyleRule)
			visit((CSSStyleRule)n);
		else if(n instanceof CSSPageRule)
			visit((CSSPageRule)n);
		else if(n instanceof CSSCharsetRule)
			visit((CSSCharsetRule)n);
		else if(n instanceof CSSFontFaceRule)
			visit((CSSFontFaceRule)n);
		else if(n instanceof CSSImportRule)
			visit((CSSImportRule)n);
		else if(n instanceof CSSMediaRule)
			visit((CSSMediaRule)n);
	}
}

package org.qrone.r7.parser;

import org.w3c.dom.Element;
import org.w3c.dom.css.CSSValue;

public class HTML5Element {
	private HTML5OM om;
	private Element e;
	public HTML5Element(HTML5OM om, Element e){
		this.om = om;
		this.e = e;
	}
	
	public HTML5OM getOM(){
		return om;
	}

	public Element get(){
		return e;
	}
	
	public String getAttribute(String name){
		return e.getAttribute(name);
	}

	public void setAttribute(String name, String value){
		e.setAttribute(name, value);
	}
	
	public String getProperty(String prop){
		return om.getProperty(e, prop);
	}
	
	public CSS3Values getPropertyValue(String prop){
		return om.getPropertyValue(e, prop);
	}
	
	public void setProperty(String prop, String value){
		om.setProperty(e, prop, value);
	}

	public void renameProperty(String prop, String newprop){
		om.renameProperty(e, prop, newprop);
	}
	
	public void removeProperty(String prop){
		om.removeProperty(e, prop);
	}
}

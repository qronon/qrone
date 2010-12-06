package org.qrone.r7.parser;

import org.w3c.dom.Element;

public interface NodeProcessor {
	public HTML5Element get(Element node);

	public void visit(HTML5Element e);

	public void out(HTML5OM o);

	public void append(String string);
	
	//public boolean isTarget(Element node);
	//public void processTarget(HTML5Writer w, HTML5OM om, Element node);
}

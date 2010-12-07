package org.qrone.r7.parser;

import org.w3c.dom.Element;

public interface NodeProcessor {
	public HTML5Element get(Element node);
	public HTML5Template newTemplate();
}

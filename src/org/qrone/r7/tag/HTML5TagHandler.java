package org.qrone.r7.tag;

import org.qrone.r7.parser.HTML5Element;

public abstract class HTML5TagHandler {
	public abstract HTML5TagResult process(HTML5Element e);
}
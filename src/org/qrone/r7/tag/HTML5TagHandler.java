package org.qrone.r7.tag;

import org.qrone.r7.parser.HTML5Element;

public interface HTML5TagHandler {
	public HTML5TagResult process(HTML5Element e);
}
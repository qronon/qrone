package org.qrone.r7.tag;

import org.qrone.r7.parser.HTML5Element;

public interface HTML5TagResult {
	public String prestart();
	public String poststart();
	public String preend();
	public String postend();
	public void process(HTML5Element e);
}
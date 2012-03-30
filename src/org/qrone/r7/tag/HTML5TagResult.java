package org.qrone.r7.tag;

import org.qrone.r7.parser.HTML5Element;
import org.w3c.dom.Element;

public interface HTML5TagResult {
	public String prestart(String ticket);
	public String poststart(String ticket);
	public String preend(String ticket);
	public String postend(String ticket);
	//public void process(String ticket);
}
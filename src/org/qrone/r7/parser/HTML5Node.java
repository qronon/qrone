package org.qrone.r7.parser;

import org.qrone.r7.parser.HTML5NodeSet.Delegate;
import org.qrone.r7.script.browser.Function;

public interface HTML5Node {

	public HTML5Node addClass(String cls);
	public HTML5Node removeClass(String cls);
	public HTML5Node attr(String prop);
	public HTML5Node attr(String prop, String value);
	public HTML5Node css(String prop);
	public HTML5Node css(String prop, String value);
	public String html();
	public HTML5Node html(HTML5Template html);
	public HTML5Node html(HTML5Node html);
	public HTML5Node html(Function html);
	public HTML5Node html(String html);
	public HTML5Node each(Function func);
	public HTML5Node append(HTML5Node html);
	public HTML5Node append(Function o);
	public HTML5Node append(String o);
	public HTML5Node prepend(HTML5Node html);
	public HTML5Node prepend(Function o);
	public HTML5Node prepend(String o);
	public Object clone();
}

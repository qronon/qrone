package org.qrone.r7.parser;

import java.util.List;
import java.util.Set;

import org.qrone.r7.parser.HTML5NodeSet.Delegate;
import org.qrone.r7.script.browser.Function;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public interface HTML5Node {
	public Object get();
	public HTML5Template getDocument();
	public HTML5Node addClass(String cls);
	public HTML5Node removeClass(String cls);
	public String attr(String prop);
	public HTML5Node attr(String prop, String value);
	public String css(String prop);
	public HTML5Node css(String prop, String value);
	public String html();
	public HTML5Node html(HTML5Template html);
	public HTML5Node html(HTML5Node html);
	public HTML5Node html(Function html);
	public HTML5Node html(String html);
	public HTML5Node each(Function func);
	public HTML5Node appendChild(HTML5Node html);
	public HTML5Node append(HTML5Node html);
	public HTML5Node append(Function o);
	public HTML5Node append(String o);
	public HTML5Node removeChild(HTML5Node html);
	public HTML5Node remove();
	public HTML5Node remove(HTML5Node html);
	public HTML5Node prepend(HTML5Node html);
	public HTML5Node prepend(Function o);
	public HTML5Node prepend(String o);
	public HTML5Node select(String o);
	public HTML5Node clone();
	public HTML5Node repeat(List list);
	public HTML5Node repeat(List list, Function repeartfunc);
}

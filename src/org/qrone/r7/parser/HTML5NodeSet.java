package org.qrone.r7.parser;

import java.util.List;
import java.util.Set;

import org.qrone.r7.script.browser.Function;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class HTML5NodeSet implements HTML5Node{
	private HTML5Template template;
	private Set<Node> set;
	
	public HTML5NodeSet(HTML5Template template, Set<Node> set) {
		this.template = template;
		this.set = set;
	}

	public HTML5Node clone(){
		return new HTML5NodeSet(template.newTemplate(),set);
	}

	public HTML5Node first(){
		Node node = set.iterator().next();
		if(node instanceof Element)
			return template.override((Element)node);
		return null;
	}
	
	private HTML5Node exec(final Delegate f){
		for (Node node : set) {
			if(node instanceof Element)
				f.call(template.override((Element)node));
		}
		return this;
	}
	
	public HTML5Node addClass(final String cls){
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.addClass(cls);
			}
		});
	}

	public HTML5Node removeClass(final String cls){
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.removeClass(cls);
			}
		});
	}
	
	public String attr(final String prop){
		return first().attr(prop);
	}
	
	public HTML5Node attr(final String prop, final String value){
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.attr(prop, value);
			}
		});
	}

	public String css(final String prop){
		return first().css(prop);
	}
	
	public HTML5Node css(final String prop, final String value){
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.css(prop, value);
			}
		});
	}

	public String html(){
		final StringBuilder b = new StringBuilder();
		exec(new Delegate() {
			public void call(HTML5Element e) {
				b.append(e.html());
			}
		});
		return b.toString();
	}
	
	public HTML5Node html(final HTML5Template html){
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.html(html);
			}
		});
	}
	
	public HTML5Node html(final HTML5Node html){
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.html(html);
			}
		});
	}
	
	public HTML5Node html(final Function html){
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.html(html);
			}
		});
	}
	
	public HTML5Node html(final String html){
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.html(html);
			}
		});
	}
	
	public HTML5Node each(final Function func){
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				func.call(e);
			}
		});
	}
	
	public static interface Delegate{
		public void call(HTML5Element e);
	}

	@Override
	public HTML5Node appendChild(final HTML5Node o) {
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.appendChild(o);
			}
		});
	}
	
	@Override
	public HTML5Node append(final HTML5Node o) {
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.append(o);
			}
		});
	}

	@Override
	public HTML5Node append(final Function o) {
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.append(o);
			}
		});
	}
	
	@Override
	public HTML5Node append(final String o) {
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.append(o);
			}
		});
	}

	@Override
	public HTML5Node prepend(final HTML5Node o) {
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.prepend(o);
			}
		});
	}

	@Override
	public HTML5Node prepend(final Function o) {
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.prepend(o);
			}
		});
	}
	
	@Override
	public HTML5Node prepend(final String o) {
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.prepend(o);
			}
		});
	}

	@Override
	public HTML5Node removeChild(final HTML5Node o) {
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.removeChild(o);
			}
		});
	}
	
	public HTML5Node remove(HTML5Node node){
		return removeChild(node);
	}
	
	@Override
	public HTML5Node remove() {
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.remove();
			}
		});
	}

	@Override
	public HTML5Node select(String o) {
		return template.select(o, this);
	}

	public Set<Node> get(){
		return set;
	}
	
	public Set<Node> get(boolean override){
		return set;
	}

	@Override
	public HTML5Node repeat(final List l) {
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.repeat(l);
			}
		});
	}
	
	@Override
	public HTML5Node repeat(final List l, final Function f) {
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.repeat(l, f);
			}
		});
	}

	@Override
	public HTML5Template getDocument() {
		return template;
	}
}

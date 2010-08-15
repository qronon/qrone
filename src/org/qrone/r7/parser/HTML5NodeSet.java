package org.qrone.r7.parser;

import java.util.Set;

import org.qrone.r7.script.browser.Function;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class HTML5NodeSet{
	private HTML5Template template;
	private Node node;
	private Set<Node> set;

	public HTML5NodeSet(HTML5Template template, Node node) {
		this.template = template;
		this.node = node;
	}
	
	public HTML5NodeSet(HTML5Template template, Set<Node> set) {
		this.template = template;
		this.set = set;
	}

	public HTML5NodeSet addClass(final String cls){
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.addClass(cls);
			}
		});
	}

	public HTML5NodeSet removeClass(final String cls){
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.removeClass(cls);
			}
		});
	}
	
	public HTML5NodeSet attr(final String prop){
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.attr(prop);
			}
		});
	}
	
	public HTML5NodeSet attr(final String prop, final String value){
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.attr(prop, value);
			}
		});
	}

	public HTML5NodeSet css(final String prop){
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.css(prop);
			}
		});
	}
	
	public HTML5NodeSet css(final String prop, final String value){
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.css(prop, value);
			}
		});
	}

	public HTML5NodeSet listup(final NodeLister func){
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.html(func);
			}
		});
	}
	
	public HTML5NodeSet html(final String html){
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				e.html(html);
			}
		});
	}
	
	public HTML5NodeSet each(final Function func){
		return exec(new Delegate() {
			public void call(HTML5Element e) {
				func.call(new HTML5NodeSet(template, e.get()));
			}
		});
	}
	
	public HTML5NodeSet exec(final Delegate f){
		if(set != null){
			for (Node node : set) {
				if(node instanceof Element)
					f.call(template.override((Element)node));
			}
		}else{
			if(node instanceof Element)
				f.call(template.override((Element)node));
		}
		return this;
	}
	
	public static interface Delegate{
		public void call(HTML5Element e);
	}

}

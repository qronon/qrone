package org.qrone.r7.parser;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import se.fishtank.css.selectors.NodeSelectorException;

public class HTML5Template implements HTML5Writer, NodeProcessor{
	private List<Object> list = new ArrayList<Object>();
	private StringBuilder b = new StringBuilder();

	public HTML5Template(){
		list.add(b);
	}
	
	public void append(String key, String value){
		b.append(value);
	}
	
	public void append(HTML5Template t){
		list.add(t);
		b = new StringBuilder();
		list.add(b);
	}

	public void append(char str){
		b.append(String.valueOf(str));
	}

	public void append(CharSequence str){
		b.append(str);
	}
	
	public void append(String str){
		b.append(str);
	}
	
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		for (Iterator<Object> i = list.iterator(); i
				.hasNext();) {
			b.append(i.next().toString());
		}
		return b.toString();
	}

	private Map<String, NodeLister> selectmap = new Hashtable<String, NodeLister>();
	private Map<Element, NodeLister> nodemap = new Hashtable<Element, NodeLister>();

	public void set(String selector, final String value){
		set(selector, new NodeLister() {
			@Override
			public void accept(HTML5Element e) {
				append(value);
			}
		});
	}
	
	public void set(String selector, NodeLister lister){
		selectmap.put(selector, lister);
	}

	@Override
	public boolean isTarget(Element node) {
		return nodemap.containsKey(node);
	}

	@Override
	public void processTarget(HTML5OM om, Element node) {
		NodeLister o = nodemap.get(node);
		if(o != null){
			o.accept(new HTML5Element(om, (Element)node));
		}
	}
	
/*
	public void process(HTML5OM om) throws NodeSelectorException{
		om.process(this);
	}

	public void process(Set<Node> nodes) throws NodeSelectorException{
		om.process(this, nodes);
	}

	public void process(Node nodes) throws NodeSelectorException{
		
	}

	public void process(String selector) throws NodeSelectorException{
		process(om.select(selector));
	}
	
	public void process(HTML5Template doc, HTML5OM om) throws NodeSelectorException{
		if(u != null){
			try {
				om = om.getDeck().compile(new URI(u));
			} catch (URISyntaxException e) {
				return;
			}
		}
		Set<Node> nodeset = om.select(s);
		for (Iterator<Node> i = nodeset.iterator(); i
				.hasNext();) {
			Node node = i.next();
			for (Iterator iterator = l.iterator(); iterator
					.hasNext();) {
				
				HTML5Template t = new HTML5Template();
				accept(t, iterator.next());
				
			}
		}
	}
	*/
	public String process(HTML5OM om) {
		for (Iterator<Entry<String, NodeLister>> iterator = selectmap.entrySet().iterator(); iterator
				.hasNext();) {
			Entry<String, NodeLister> e = iterator.next();
			try{
				Set<Node> set = om.select(e.getKey());
				for (Iterator<Node> iter = set.iterator(); iter.hasNext();) {
					Node n = iter.next();
					if(n instanceof Element)
						nodemap.put((Element)n, e.getValue());
				}
			}catch(NodeSelectorException e1){}
		}
		
		om.process(this);
		return toString();
	}
	
}

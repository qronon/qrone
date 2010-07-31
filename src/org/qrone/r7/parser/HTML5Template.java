package org.qrone.r7.parser;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.qrone.r7.ObjectConverter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class HTML5Template implements HTML5Writer, NodeProcessor{
	private List<Object> list = new ArrayList<Object>();
	private StringBuilder b = new StringBuilder();
	
	private HTML5OM om;
	private Set<HTML5OM> xomlist;
	private URI uri;
	
	private HTML5Template(HTML5OM om, Set<HTML5OM> xomlist, URI uri){
		this.uri = uri;
		this.om = om;
		this.xomlist = xomlist;
		list.add(b);
	}

	public HTML5Template(HTML5OM om, URI uri){
		this(om, new HashSet<HTML5OM>(), uri);
	}
	
	public HTML5Template(HTML5OM om){
		this(om, new HashSet<HTML5OM>(), null);
	}

	public HTML5Template append(){
		HTML5Template t = new HTML5Template(om, xomlist, uri);
		list.add(t);
		b = new StringBuilder();
		list.add(b);
		return t;
	}

	public void append(String key, String value){
		if(value != null)
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

	private boolean initialized = false;
	private Map<String, NodeLister> selectmap = new Hashtable<String, NodeLister>();
	private Map<Element, NodeLister> nodemap;
	public void set(Object o){
		if(o instanceof Map){
			for (Iterator iterator = ((Map)o).entrySet().iterator(); iterator
					.hasNext();) {
				set(iterator.next());
			}
		}else if(o instanceof Entry){
			Entry e = (Entry)o;
			set("#" + e.getKey(), e.getValue());
		}
	}

	public void set(final String selector, final Object o){
		if(o instanceof HTML5Element){
			set(selector, new NodeLister(){
				@Override
				public void accept(HTML5Template t, HTML5Element e) {
					t.out((HTML5Element)o);
				}
			});
		}else if(o instanceof Map){
			set(selector, new NodeLister(){
				@Override
				public void accept(HTML5Template t, HTML5Element e) {
					for (Iterator iterator = ((Map)o).entrySet().iterator(); iterator
					.hasNext();) {
						Entry el = (Entry)iterator.next();
						t.set(selector + " .key", el.getKey());
						t.set(selector + " .value", el.getValue());
						t.out(e);
					}
				}
			});
		}else if(o instanceof List){
			set(selector, new NodeLister() {
				@Override
				public void accept(HTML5Template t, HTML5Element e) {
					for (Iterator iterator = ((List)o).iterator(); iterator
							.hasNext();) {
						t.set(iterator.next());
						t.out(e);
					}
				}
			});
		}else{
			set(selector, o.toString().replaceAll("\n", "<br>").replaceAll(" ", "&nbsp;"));
		}
	}
	
	public void set(String selector, final String value){
		set(selector, new NodeLister() {
			@Override
			public void accept(HTML5Template t, HTML5Element e) {
				t.append(value);
			}
		});
	}
	
	public void set(String selector, NodeLister lister){
		selectmap.put(selector, lister);
		initialized = false;
	}

	@Override
	public boolean isTarget(Element node) {
		return nodemap.containsKey(node);
	}

	@Override
	public void processTarget(HTML5Writer w, HTML5OM om, Element node) {
		NodeLister o = nodemap.get(node);
		if(o != null){
			HTML5Template t = new HTML5Template(om, xomlist, uri);
			o.accept(t, new HTML5Element(om, (Element)node));
			w.append(t);
		}
	}
	
	private Map<String, Iterator<Node>> selecting
		= new Hashtable<String, Iterator<Node>>();
	public void visit(String selector){
		initialize(om);
		if(selecting.containsKey(selector)){
			Iterator<Node> iter = selecting.get(selector);
			if(iter != null){
				if(!iter.hasNext())
					iter = om.select(selector).iterator();
				om.process(this, this, iter.next(), null, xomlist);
			}
		}else{
			Set<Node> nodes = om.select(selector);
			if(nodes != null && !nodes.isEmpty()){
				Iterator<Node> iter = nodes.iterator();
				selecting.put(selector, iter);
				om.process(this, this, iter.next(), null, xomlist);
			}else{
				selecting.put(selector, null);
			}
		}
		selectmap = new Hashtable<String, NodeLister>();
	}
	
	public void visit(HTML5Element e){
		initialize(om);
		e.getOM().process(this, this, e.get(), null, xomlist);
		selectmap = new Hashtable<String, NodeLister>();
	}

	public void visit(HTML5OM om) {
		initialize(om);
		om.process(this, this, null, null, xomlist);
		selectmap = new Hashtable<String, NodeLister>();
	}
	
	public void out(HTML5Element e) {
		visit(e);
	}
	
	public String out() {
		initialize(om);
		om.process(this, xomlist);
		return toString();
	}
	
	private void initialize(HTML5OM om){
		if(!initialized){
			nodemap = new Hashtable<Element, NodeLister>();
			for (Iterator<Entry<String, NodeLister>> iterator = selectmap.entrySet().iterator(); iterator
					.hasNext();) {
				Entry<String, NodeLister> e = iterator.next();
				Set<Node> set = om.select(e.getKey());
				if(set != null){
					for (Iterator<Node> iter = set.iterator(); iter.hasNext();) {
						Node n = iter.next();
						if(n instanceof Element)
							nodemap.put((Element)n, e.getValue());
					}
				}
			}
		}
	}

	public HTML5Element getBody() {
		return om.getBody();
	}
	
	public void write(String str){
		append(str);
	}
	
	public void write(Object out) throws IOException{
		append(ObjectConverter.stringify(out));
	}

	public void writeln(Object out) throws IOException{
		write(out);
		write("\n");
	}
	
	public void writeln(String out) throws IOException{
		write(out);
		write("\n");
	}

	public URI getURI() {
		return uri;
	}
	
}

package org.qrone.r7.parser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.arnx.jsonic.JSON;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class HTML5Template implements HTML5Writer, NodeProcessor{
	
	private List<Object> list = new ArrayList<Object>();
	private StringBuilder b = new StringBuilder(10240);
	
	protected HTML5OM om;
	protected Set<HTML5OM> xomlist;
	protected URI uri;
	
	protected HTML5Template(HTML5OM om, Set<HTML5OM> xomlist, URI uri){
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
	
	public HTML5Template(HTML5Deck deck, String path){
		this(null, new HashSet<HTML5OM>(), null);
		if(deck.getResolver().exist(path)){
			try {
				om = deck.compile(new URI(path));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void load(String path) throws IOException, URISyntaxException{
		URI u = uri.resolve(path);
		if(om.getDeck().getResolver().exist(u.toString())){
			om = om.getDeck().compile(u);
		}
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
	
	public HTML5NodeSet select(String selector){
		return new HTML5NodeSet(this, om.select(selector));
	}

	private Map<Element, HTML5Element> node5map = new Hashtable<Element, HTML5Element>();
	public HTML5Element override(Element node){
		HTML5Element e = node5map.get(node);
		if(e == null){
			e = new HTML5Element(om, node);
			node5map.put(node, e);
		}
		return e;
	}

	@Override
	public HTML5Element get(Element node) {
		HTML5Element e = node5map.get(node);
		if(e == null){
			e = new HTML5Element(om, node);
		}
		return e;
	}
	
	/*
	private void set(Element node, String html){
		HTML5Element e = node5map.get(node);
		if(e == null){
			e = new HTML5Element(om, node);
			node5map.put(node, e);
		}
		e.html(html);
	}
	
	private void set(Element node, NodeLister lister){
		HTML5Element e = node5map.get(node);
		if(e == null){
			e = new HTML5Element(om, node);
			node5map.put(node, e);
		}
		e.list(lister);
	}
	*/
	
	public void set(Object o){
		if(o instanceof List){
			for(Object i : (List)o){
				set(i);
			}
		}else if(o instanceof Map){
			for (Object i : ((Map)o).entrySet()) {
				set(i);
			}
		}else if(o instanceof Entry){
			Entry e = (Entry)o;
			set("#" + e.getKey(), e.getValue());
		}
	}
	
	public void set(final String selector, final Object o){
		if(o instanceof Map){
			select(selector).exec(new HTML5NodeSet.Delegate() {
				public void call(HTML5Element e) {
					e.html(new NodeLister() {
						@Override
						public void accept(HTML5Template t, HTML5Element e) {
							Set<Entry> entryset = ((Map)o).entrySet();
							for (Entry el : entryset) {
								HTML5Template tt = new HTML5Template(om, xomlist, uri);
								tt.set(selector + ".key", el.getKey());
								tt.set(selector + ".value", el.getValue());
								tt.visit(e);
								t.append(tt);
							}
						}
					});
				}
			});
		}else if(o instanceof List){
			select(selector).exec(new HTML5NodeSet.Delegate() {
				public void call(HTML5Element e) {
					e.html(new NodeLister() {
						@Override
						public void accept(HTML5Template t, HTML5Element e) {
							for (Iterator iterator = ((List)o).iterator(); iterator
									.hasNext();) {
								HTML5Template tt = new HTML5Template(om, xomlist, uri);
								tt.set(iterator.next());
								tt.visit(e);
								t.append(tt);
							}
						}
					});
				}
			});
		}else if(o instanceof NodeLister){
			select(selector).listup((NodeLister)o);
		}else{
			select(selector).html(o.toString().replaceAll("\n", "<br>"));
		}
	}

	public void set(String selector, NodeLister lister){
		select(selector).listup(lister);
	}
	/*
	private boolean initialized = false;
	private Map<String, NodeLister> selectmap = new Hashtable<String, NodeLister>();
	private Map<Element, NodeLister> nodemap;
	
	public void set(String selector, final String value){
		set(selector, new NodeLister() {
			@Override
			public void accept(HTML5Template t, HTML5Element e) {
				t.append(value);
			}
		});
	}
	
	*/

	/*
	@Override
	public boolean isTarget(Element node) {
		return node5map.containsKey(node);
	}

	@Override
	public void processTarget(HTML5Writer w, HTML5OM om, Element node) {
		HTML5Element o = node5map.get(node);
		if(o != null){
			HTML5Template t = new HTML5Template(om, xomlist, uri);
			o.accept(t);
			
			w.append(t);
		}
	}
	*/

	
	public void visit(HTML5Element e){
		//initialize(om);
		e.getOM().process(this, this, e.get(), null, xomlist);
	}

	public void visit(HTML5OM om) {
		//initialize(om);
		om.process(this, this, null, null, xomlist);
	}

	
	private Map<String, Iterator<Node>> selecting
		= new Hashtable<String, Iterator<Node>>();
	public void out(String selector){
		//initialize(om);
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
	}
	
	public void out(HTML5NodeSet e) {
		e.exec(new HTML5NodeSet.Delegate() {
			@Override
			public void call(HTML5Element e) {
				visit(e);
			}
		});
	}
	
	public void out(HTML5Element e) {
		visit(e);
	}
	
	public void out() {
		//initialize(om);
		om.process(this, xomlist);
	}
	
	/*
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
			initialized = true;
		}
	}
	*/

	public HTML5Element getBody() {
		return om.getBody();
	}
	
	/*
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
	*/

	public URI getURI() {
		return uri;
	}

	public void write(Object out) throws IOException{
		if(out instanceof String)
			append((String)out);
		else
			append(JSON.encode(out));
	}

	public void writeln(Object out) throws IOException{
		write(out);
		write("\n");
	}
	
}

package org.qrone.r7.parser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.arnx.jsonic.JSON;

import org.qrone.util.QrONEUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import se.fishtank.css.selectors.NodeSelectorException;
import se.fishtank.css.selectors.dom.DOMNodeSelector;

public class HTML5Template{
	
	protected HTML5OM om;
	protected Set<HTML5OM> xomlist;
	protected URI uri;
	protected boolean loaded = false;
	protected String id;
	
	protected HTML5Template(HTML5OM om, Set<HTML5OM> xomlist, URI uri){
		this.uri = uri;
		this.om = om;
		this.xomlist = xomlist;
		if(om != null)
			loaded = true;
	}

	public HTML5Template(HTML5OM om, URI uri){
		this(om, new HashSet<HTML5OM>(), uri);
		id = QrONEUtils.uniqueid();
	}
	
	public HTML5Template(HTML5OM om){
		this(om, new HashSet<HTML5OM>(), null);
	}
	
	protected HTML5Template(HTML5Deck deck, String path) throws IOException{
		this(null, new HashSet<HTML5OM>(), null);
		if(deck.getResolver().exist(path)){
			try {
				uri = new URI(path);
				om = deck.compile(uri);
				loaded = true;
			} catch (URISyntaxException e) {
			}
		}
	}
	
	public HTML5Template(HTML5Template t, HTML5OM om, URI uri) {
		this.uri = uri;
		this.om = om;
		this.xomlist = t.xomlist;
		xomlist.add(om);
		if(om != null)
			loaded = true;
	}

	public void load(String path) throws IOException, URISyntaxException{
		URI u = uri.resolve(path);
		if(om.getDeck().getResolver().exist(u.toString())){
			om = om.getDeck().compile(u);
		}
	}
	
	public boolean isLoaded(){
		return loaded;
	}
	
	public Object $(String selector){
		return select(selector);
	}

	public Object $(String selector, HTML5Node node){
		return select(selector, node);
	}
	
	public HTML5NodeSet select(String selector){
		return new HTML5NodeSet(this, om.select(selector));
	}

	public HTML5NodeSet select(String selector, HTML5Node node){
		if( node.getDocument() != this ){
			return node.getDocument().select(selector, node);
		}
		return new HTML5NodeSet(this, om.select(selector, node.get(false)));
	}

	private Map<Element, HTML5Element> node5map = new Hashtable<Element, HTML5Element>();
	public HTML5Element override(Element node){
		HTML5Element e = node5map.get(node);
		if(e == null){
			e = new HTML5Element(om, this, node);
			node5map.put(node, e);
		}
		return e;
	}
	
	public HTML5Element get(Element node) {
		return node5map.get(node);
	}

	public HTML5Node getElementsByTagName(String tagName){
		return select(tagName);
	}
	
	public HTML5Node getElementsByClassName(String clsName){
		return select("." + clsName);
	}
	
	public HTML5Node getElementById(String id){
		return select("#" + id);
	}
	
	public void out(HTML5Writer w, HTML5NodeSet set, String ticket) {
		final String uniqueid = QrONEUtils.uniqueid();
		for (Iterator<Node> iter = set.get().iterator(); iter.hasNext();) {
			om.process(w, this, iter.next(), uniqueid, xomlist, ticket);
			
		}
	}
	
	public void out(HTML5Writer w, Element e, String ticket) {
		final String uniqueid = QrONEUtils.uniqueid();
		om.process(w, this, e, uniqueid, xomlist, ticket);
	}
	
	public void out(HTML5Writer w, HTML5Element e, String ticket) {
		final String uniqueid = QrONEUtils.uniqueid();
		om.process(w, this, e.get(), uniqueid, xomlist, ticket);
	}
	
	public void out(HTML5Writer w, Document e, String ticket) {
		final String uniqueid = QrONEUtils.uniqueid();
		om.process(w, this, e, uniqueid, xomlist, ticket);
	}

	public HTML5Element getBody() {
		return new HTML5Element(om, this, om.getBody());
	}

	public URI getURI() {
		return uri;
	}
	
	public HTML5Template newTemplate() {
		HTML5Template t =  new HTML5Template(om, xomlist, uri);
		//t.id = id;
		//t.node5map = node5map;
		return t;
	}
	
}

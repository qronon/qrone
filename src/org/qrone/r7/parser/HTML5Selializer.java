package org.qrone.r7.parser;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.qrone.r7.parser.HTML5Deck.HTML5Set;
import org.qrone.r7.script.browser.Function;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class HTML5Selializer extends HTML5TagWriter{
	static{
		noendtaglist.addAll(Arrays.asList(noendtags));
		noendtaglist.addAll(Arrays.asList(nnendtags));
	}
	
	int formatting = 0;
	boolean inScript;
	boolean inHead;
	
	public HTML5Selializer(
			Element body, 
			HTML5Set set, HTML5Deck deck, 
			Node node, URI uri, 
			HTML5Writer w, HTML5Template t, HTML5OM om, String id, String ticket){
		super(w, id, uri, ticket);
		
		this.body = body;
		this.set = set;
		this.node = node;
		this.deck = deck;
		this.t = t;
		this.om = om;
	}
	
	private Element body;
	private HTML5Set set;
	private Node node;
	private HTML5Deck deck;
	private URI uri;
	private HTML5OM om;
	private HTML5Template t;
	
	@Override
	public void visit(Document e) {
		append("<!DOCTYPE html>");
		super.visit(e);
		
	}
	
	@Override
	public void visit(Element e) {
		if(e.getNodeName().equals("head")){
			start(e);
			inHead = true;
			accept(e);
			inHead = false;
			deck.outputStyles(b, set, uri);
			end(e);
		}else if(e.getNodeName().equals("body")){
			if(node != body){
				start(e);
			}
			
			accept(e);
			
			if(node != body){
				deck.outputScripts(b, set, uri);
				end(e);
			}
		}else if(e.getNodeName().equals("script")){
			if(!inHead){
				start(e);
				inScript = true;
				accept(e);
				inScript = false;
				end(e);
			}
		}else if(e.getNodeName().equals("style")){
		}else if(e.getNodeName().equals("link")){
		}else if(e.getNodeName().equals("meta")){
			if(e.getAttribute("name").equals("extends")){
			}else{
				start(e);
				accept(e);
				end(e);
			}
		}else if(e.getNodeName().equals("pre") || e.getNodeName().equals("code") || e.getNodeName().equals("textarea")){
			formatting++;
			start(e);
			accept(e);
			end(e);
			formatting--;
		}else if(e.getNodeName().equals("meta")){
			if(e.getAttribute("name").equals("extends")){
			}else{
				start(e);
				accept(e);
				end(e);
			}
		}else if(e.getNodeName().equals("form")){
			start(e);
			accept(e);
			if(ticket != null){
				append("<input type=\"hidden\" name=\".ticket\" value=\"" + ticket + "\"/>");
			}
			end(e);
		}else{
			start(e);
			accept(e);
			end(e);
		}
		
		
	}

	@Override
	public void visit(Text n) {
		if(inScript){
			append(n.getNodeValue());
		}else if(formatting>0){
			append_pre(n.getNodeValue());
		}else{
			append(escape(n.getNodeValue()));
		}
	}

	protected void accept(Element e) {
		if(t != null){
			HTML5Element node = t.get(e);
			if(node == null){
				super.accept(e);
			}else{
				accept(node);
			}
		}
	}

	private void accept(List nodes){
		if(nodes != null){
			for (Iterator iter = nodes.iterator(); iter.hasNext();) {
				dispatch(iter.next());
			}
		}
	}
	
	private void accept(HTML5Element e){
		if(e.content == null){
			HTML5Template et = e.getDocument();
			if(et == t){
				super.accept(e.get());
			}else{
				et.out(b, e, ticket);
			}
		}else{
			dispatch(e.content);
		}
	}
	
	private void dispatch(Object o) {
		if(o instanceof Set){
			Set<Node> set = (Set<Node>)o;
			for (Iterator iter2 = set.iterator(); iter2.hasNext();) {
				Node node = (Node) iter2.next();
				dispatch(node);
			}
		}else if(o instanceof Element){
			accept((Element)o);
		}else if(o instanceof HTML5Element){
			accept((HTML5Element)o);
		}else if(o instanceof HTML5NodeSet){
			HTML5NodeSet set = (HTML5NodeSet)o;
			HTML5Template et = set.getDocument();
			if(et == t){
				dispatch(set.get());
			}else{
				et.out(b, set, ticket);
			}
		}else if(o instanceof HTML5Template){
			HTML5Template t = (HTML5Template)o;
			t.out(b, t.getBody(), ticket);
		}else{
			append(o.toString());
		}
	}
}

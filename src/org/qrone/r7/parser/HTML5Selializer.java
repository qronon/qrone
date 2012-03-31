package org.qrone.r7.parser;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.qrone.r7.parser.HTML5Deck.HTML5Set;
import org.qrone.r7.script.browser.Function;
import org.qrone.r7.tag.HTML5TagResult;
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
			HTML5Template t, HTML5OM om, String id, String ticket){
		this.body = body;
		this.set = set;
		this.node = node;
		this.deck = deck;
		this.uri = uri;
		this.b = this.t = t;
		this.om = om;
		this.id = id;
		this.ticket = ticket;
	}
	
	private Element body;
	private HTML5Set set;
	private Node node;
	private HTML5Deck deck;
	private URI uri;
	@Override
	public void visit(Document e) {
		writec("<!DOCTYPE html>");
		super.visit(e);
		
	}
	
	@Override
	public void visit(Element e) {
		List<HTML5TagResult> r = getTagResult(e);
		if(e.getNodeName().equals("head")){
			start(e,r);
			inHead = true;
			accept(e);
			inHead = false;
			deck.outputStyles(b, set, uri);
			end(e,r);
		}else if(e.getNodeName().equals("body")){
			if(node != body){
				start(e,r);
			}
			
			accept(e);
			
			if(node != body){
				deck.outputScripts(b, set, uri);
				end(e,r);
			}
		}else if(e.getNodeName().equals("script")){
			if(!inHead){
				start(e,r);
				inScript = true;
				accept(e);
				inScript = false;
				end(e,r);
			}
		}else if(e.getNodeName().equals("style")){
		}else if(e.getNodeName().equals("link")){
		}else if(e.getNodeName().equals("meta")){
			if(e.getAttribute("name").equals("extends")){
			}else{
				start(e,r);
				accept(e);
				end(e,r);
			}
		}else if(e.getNodeName().equals("pre") || e.getNodeName().equals("code") || e.getNodeName().equals("textarea")){
			formatting++;
			start(e,r);
			accept(e);
			end(e,r);
			formatting--;
		}else if(e.getNodeName().equals("meta")){
			if(e.getAttribute("name").equals("extends")){
			}else{
				start(e,r);
				accept(e);
				end(e,r);
			}
		}else{
			start(e,r);
			accept(e);
			end(e,r);
		}
	}

	@Override
	public void visit(Text n) {
		if(inScript){
			write(JSParser.compress(n.getNodeValue(), true)
					.replace("__QRONE_PREFIX_NAME__", 
							"qrone[\"" + uri.toString() + "\"]"));
		}else if(formatting>0){
			writeraw(n.getNodeValue());
		}else{
			write(n.getNodeValue());
		}
	}

	private List<HTML5TagResult> getTagResult(Element e) {
		List<HTML5TagResult> l = om.getTagResult(e);
		if(t == null) return l;
		
		final HTML5Element n = t.get(e);
		if(n != null){
			if(l == null){
				l = new ArrayList<HTML5TagResult>();
			}
			l.add(new HTML5TagResult() {
				@Override
				public String prestart(String ticket) {
					accept(n.before);
					return null;
				}
				
				@Override
				public String preend(String ticket) {
					accept(n.append);
					return null;
				}
				
				@Override
				public String poststart(String ticket) {
					accept(n.prepend);
					return null;
				}
				
				@Override
				public String postend(String ticket) {
					accept(n.after);
					return null;
				}
			});
		}
		return l;
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
			accept(e.get());
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
		}else if(o instanceof HTML5Template){
			HTML5Template t = (HTML5Template)o;
			t.out();
			writec(t.toString());
		}else if(o instanceof HTML5Element){
			accept((HTML5Element)o);
		}else if(o instanceof HTML5NodeSet){
			HTML5NodeSet set = (HTML5NodeSet)o;
			dispatch(set.get());
		}else{
			writec(o.toString());
		}
	}
}

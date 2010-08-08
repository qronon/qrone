package org.qrone.r7.script;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.Scriptable;
import org.qrone.r7.handler.OpenIDHandler;
import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.JSDeck;
import org.qrone.r7.parser.JSOM;
import org.qrone.r7.resolver.URIResolver;

public class ServletScope {
	public HttpServletRequest request;
	public HttpServletResponse response;
	public OpenIDHandler handler;
	public Scriptable scope;
	public HTML5Deck deck;
	public JSDeck vm;
	public URI uri;

	public URIResolver resolver;
	public String path;
	public PrintWriter writer;
	public Set<JSOM> required = new ConcurrentSkipListSet<JSOM>();
	
	public ServletScope(HttpServletRequest request, HttpServletResponse response, 
			Scriptable scope, HTML5Deck deck, JSDeck vm, URI uri, OpenIDHandler handler) {
		this.request = request;
		this.response = response;
		this.scope = scope;
		this.deck = deck;
		this.vm = vm;
		this.uri = uri;
		this.handler = handler;
		
		resolver = deck.getResolver();
		path = uri.toString();
		try {
			writer = response.getWriter();
		} catch (IOException e) {
		}
	}
}
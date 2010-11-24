package org.qrone.r7.script;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.Scriptable;
import org.qrone.r7.PortingService;
import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.JSDeck;
import org.qrone.r7.parser.JSOM;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.script.browser.LoginService;

public class ServletScope {
	public HttpServletRequest request;
	public HttpServletResponse response;
	public Scriptable scope;
	public HTML5Deck deck;
	public JSDeck vm;
	public PortingService service;
	public URIResolver resolver;

	public URI uri;
	public String path;
	public String pathArg;

	public PrintWriter writer;
	public Set<JSOM> required = new ConcurrentSkipListSet<JSOM>();
	
	
	public ServletScope(HttpServletRequest request, HttpServletResponse response, 
			String path, String pathArg,
			Scriptable scope, HTML5Deck deck, JSDeck vm, URI uri, PortingService service) {
		this.request = request;
		this.response = response;
		this.scope = scope;
		this.deck = deck;
		this.vm = vm;
		this.uri = uri;
		this.service = service;
		
		resolver = deck.getResolver();
		this.path = path;
		this.pathArg = pathArg;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
		}
	}
}
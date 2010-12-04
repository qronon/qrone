package org.qrone.r7.handler;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.qrone.r7.Extendable;
import org.qrone.r7.PortingService;
import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.HTML5OM;
import org.qrone.r7.parser.JSDeck;
import org.qrone.r7.parser.JSOM;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.script.ServletScope;
import org.qrone.r7.script.browser.Window;

public class HTML5Handler implements URIHandler{
	private PortingService services;
	private URIResolver resolver;
	private HTML5Deck deck;
	
	public HTML5Handler(PortingService services, HTML5Deck deck) {
		this.services = services;
		this.resolver = services.getURIResolver();
		this.deck = deck;
	}

	@Override
	public boolean handle(HttpServletRequest request, HttpServletResponse response, 
			String uri, String path, String pathArg) {
		try {
			if(resolver.exist(path + ".html")){
				URI urio = new URI(path + ".html");
				HTML5OM om = deck.compile(urio);
				if(om != null){
					response.setContentType("text/html; charset=utf8");
					deck.getSpriter().create();

					Writer out = response.getWriter();
					out.append(om.serialize());
					out.flush();
					out.close();
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}

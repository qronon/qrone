package org.qrone.r7.handler;

import java.io.BufferedWriter;
import java.io.Writer;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.r7.PortingService;
import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.HTML5OM;
import org.qrone.r7.parser.HTML5ScriptWriter;
import org.qrone.r7.parser.HTML5StreamWriter;
import org.qrone.r7.parser.HTML5StringWriter;
import org.qrone.r7.parser.HTML5Template;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.script.browser.User;

public class HTML5PartsHandler implements URIHandler{
	private URIResolver resolver;
	private HTML5Deck deck;
	
	public HTML5PartsHandler(PortingService services, HTML5Deck deck) {
		this.resolver = services.getURIResolver();
		this.deck = deck;
	}

	@Override
	public boolean handle(HttpServletRequest request, HttpServletResponse response, 
			String uri, String path, String leftpath) {
		try {
			if(resolver.exist(uri)){
				URI urio = new URI(uri);
				HTML5OM om = deck.compile(urio);
				if(om != null){
					response.setContentType("text/javascript; charset=utf8");

					Writer out = response.getWriter();
					
					User user = (User)request.getAttribute("User");
					
					
					HTML5ScriptWriter w = new HTML5ScriptWriter(uri);
					HTML5Template t = new HTML5Template(om, urio);
					t.out(w, om.getBody(), user.getTicket());
					
					out.append(w.toString());
					out.append('\n');
					out.append(om.getScripts(false));
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

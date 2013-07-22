package org.qrone.r7.handler;

import java.io.BufferedWriter;
import java.io.Writer;
import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.login.AccessToken;
import org.qrone.r7.PortingService;
import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.HTML5OM;
import org.qrone.r7.parser.HTML5StreamWriter;
import org.qrone.r7.parser.HTML5StringWriter;
import org.qrone.r7.parser.HTML5Template;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.script.browser.User;

public class HTML5Handler implements URIHandler{
	private URIResolver resolver;
	private HTML5Deck deck;
	
	public HTML5Handler(PortingService services, HTML5Deck deck) {
		this.resolver = services.getURIResolver();
		this.deck = deck;
	}

	@Override
	public boolean handle(HttpServletRequest request, HttpServletResponse response, 
			String uri, String path, String leftpath, List<String> arg) {
		try {
			if(resolver.exist(uri)){
				URI urio = new URI(uri);
				HTML5OM om = deck.compile(urio);
				if(om != null){
					response.setContentType("text/html; charset=utf8");

					Writer out = response.getWriter();
					
					User user = (User)request.getAttribute("User");
					
					Writer bufw = new BufferedWriter(out);
					HTML5StreamWriter w = new HTML5StreamWriter(bufw);
					HTML5Template t = new HTML5Template(om, urio);
					t.out(w, om.getDocument(), user.getTicket(AccessToken.WRITE));
					bufw.flush();
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

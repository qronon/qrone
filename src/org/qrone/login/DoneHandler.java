package org.qrone.login;

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
import org.qrone.r7.handler.URIHandler;
import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.HTML5OM;
import org.qrone.r7.parser.JSDeck;
import org.qrone.r7.parser.JSOM;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.script.ServletScope;
import org.qrone.r7.script.browser.Window;

public class DoneHandler implements URIHandler{
	private PortingService services;
	
	public DoneHandler(PortingService services) {
		this.services = services;
	}

	@Override
	public boolean handle(HttpServletRequest request, HttpServletResponse response, 
			String uri, String path, String pathArg) {
		String done = request.getParameter(".done");
		if(done != null && services.getSecurityService().isSecured(request)){
			try {
				response.sendRedirect(done);
				return true;
			} catch (IOException e) {
			}
		}
		return false;
	}
}

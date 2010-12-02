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

public class DefaultHandler implements URIHandler, Extendable{
	private PortingService services;
	private URIResolver resolver;
	private HTML5Deck deck;
	private JSDeck vm;
	private URIHandler pathFinderHandler;
	private URIHandler html5Handler;
	private URIHandler jsHandler;
	private URIHandler resolverHandler;
	private URIHandler doneHandler;
	private URIHandler handler;
	
	public DefaultHandler(PortingService services, URIHandler finalizer) {
		this.services = services;
		this.resolver = services.getURIResolver();
		deck = new HTML5Deck(resolver, services.getImageBufferService());
		vm = new JSDeck(resolver, deck);
		
		html5Handler = new HTML5Handler(services, deck);
		jsHandler = new JavaScriptHandler(services, vm, deck);
		resolverHandler = new ResolverHandler(resolver);
		doneHandler = finalizer;
		handler = new URIHandler() {
			@Override
			public boolean handle(HttpServletRequest request,
					HttpServletResponse response, String uri, String path,
					String pathArg) {
				return mainHandle(request, response, uri, path, pathArg);
			}
		};
		pathFinderHandler = new PathFinderHandler(handler);
	}
	
	public void addExtension(Class c){
		vm.addExtension(c);
		deck.addExtension(c);
	}
	
	public boolean mainHandle(HttpServletRequest request, HttpServletResponse response, 
			String uri, String path, String pathArg) {

		try {
			deck.update(new URI(path));
			response.setCharacterEncoding("utf8");
			
			
			if(uri.endsWith(".server.js") && 
					jsHandler.handle(request, response, uri, path, pathArg)){
				doneHandler.handle(request, response, uri, path, pathArg);
				return true;
			}
			
			if(jsHandler.handle(request, response, uri + ".server.js", path, pathArg)){
				doneHandler.handle(request, response, uri, path, pathArg);
				return true;
			}
			
			if(html5Handler.handle(request, response, uri, path, pathArg)){
				doneHandler.handle(request, response, uri, path, pathArg);
				return true;
			}

			if(html5Handler.handle(request, response, uri + ".html", path, pathArg)){
				doneHandler.handle(request, response, uri, path, pathArg);
				return true;
			}
			
			if(html5Handler.handle(request, response, uri + ".htm", path, pathArg)){
				doneHandler.handle(request, response, uri, path, pathArg);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!uri.endsWith(".server.js") && 
				resolverHandler.handle(request, response, uri, path, pathArg)){
			return true;
		}
		return false;
	}

	@Override
	public boolean handle(HttpServletRequest request, HttpServletResponse response, 
			String uri, String path, String pathArg) {
		if(pathFinderHandler.handle(request, response, uri, path, pathArg)){
			return true;
		}
		return false;
	}
}

package org.qrone.r7.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.r7.Extendable;
import org.qrone.r7.PortingService;
import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.JSDeck;
import org.qrone.r7.resolver.URIResolver;

public class DefaultHandler implements URIHandler, Extendable{
	private PortingService services;
	private URIResolver resolver;
	private HTML5Deck deck;
	private JSDeck vm;
	private URIHandler pathFinderHandler;
	private URIHandler html5Handler;
	private URIHandler jsHandler;
	private URIHandler resolverHandler;
	private URIHandler handler;
	
	public DefaultHandler(PortingService services) {
		this.services = services;
		this.resolver = services.getURIResolver();
		deck = new HTML5Deck(services);
		vm = new JSDeck(resolver, deck);
		
		html5Handler = new HTML5Handler(services, deck);
		jsHandler = new JavaScriptHandler(services, vm, deck);
		resolverHandler = new ResolverHandler(resolver);
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
			String uri, String path, String leftpath) {

		try {
			response.setCharacterEncoding("utf8");
			
			if(uri.endsWith(".server.js") && 
					jsHandler.handle(request, response, uri, path, leftpath)){
				return true;
			}
			
			if(jsHandler.handle(request, response, uri + ".server.js", path, leftpath)){
				return true;
			}
			
			if(uri.endsWith(".html") && 
					html5Handler.handle(request, response, uri, path, leftpath)){
				return true;
			}

			if(html5Handler.handle(request, response, uri + ".html", path, leftpath)){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!uri.endsWith(".server.js") && 
				resolverHandler.handle(request, response, uri, path, leftpath)){
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
		
		if(jsHandler.handle(request, response, "/404.server.js", path, pathArg)){
			return true;
		}

		if(html5Handler.handle(request, response, "/404.html", path, pathArg)){
			return true;
		}
		
		response.setStatus(404);
		return false;
	}
}

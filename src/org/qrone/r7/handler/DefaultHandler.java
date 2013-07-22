package org.qrone.r7.handler;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.r7.Extendable;
import org.qrone.r7.PortingService;
import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.script.ServerJSDeck;

public class DefaultHandler implements URIHandler, Extendable{
	private URIResolver resolver;
	private HTML5Deck deck;
	private ServerJSDeck vm;
	private URIHandler pathFinderHandler;
	private URIHandler html5Handler;
	private URIHandler html5partsHandler;
	private URIHandler jsHandler;
	private URIHandler resolverHandler;
	private URIHandler handler;
	
	public DefaultHandler(PortingService services) {
		this.resolver = services.getURIResolver();
		deck = new HTML5Deck(services);
		vm = new ServerJSDeck(resolver, deck);

		html5Handler = new HTML5Handler(services, deck);
		html5partsHandler = new HTML5PartsHandler(services, deck);
		jsHandler = new JavaScriptHandler(services, vm, deck);
		resolverHandler = new ResolverHandler(resolver);
		handler = new URIHandler() {
			@Override
			public boolean handle(HttpServletRequest request,
					HttpServletResponse response, String uri, String path,
					String pathArg, List<String> arg) {
				return mainHandle(request, response, uri, path, pathArg, arg);
			}
		};
		pathFinderHandler = new PathFinderHandler(handler, resolver);
	}
	
	public void addExtension(Class c){
		vm.addExtension(c);
	}
	
	public boolean mainHandle(HttpServletRequest request, HttpServletResponse response, 
			String uri, String path, String leftpath, List<String> arg) {

		try {
			response.setCharacterEncoding("utf8");
			
			if(uri.endsWith(".server.js") && 
					jsHandler.handle(request, response, uri, path, leftpath, arg)){
				return true;
			}
			
			if(jsHandler.handle(request, response, uri + ".server.js", path, leftpath, arg)){
				return true;
			}
			
			if(uri.endsWith(".html") && 
					html5Handler.handle(request, response, uri, path, leftpath, arg)){
				return true;
			}

			if(html5Handler.handle(request, response, uri + ".html", path, leftpath, arg)){
				return true;
			}

			if(uri.endsWith(".js") && html5partsHandler.handle(request, response, uri.substring(0, uri.length()-3) + ".html", path, leftpath, arg)){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!uri.endsWith(".server.js") && 
				resolverHandler.handle(request, response, uri, path, leftpath, arg)){
			return true;
		}
		return false;
	}

	@Override
	public boolean handle(HttpServletRequest request, HttpServletResponse response, 
			String uri, String path, String pathArg, List<String> arg) {
		if(pathFinderHandler.handle(request, response, uri, path, pathArg, arg)){
			return true;
		}
		
		if(jsHandler.handle(request, response, "/404.server.js", path, pathArg, arg)){
			return true;
		}

		if(html5Handler.handle(request, response, "/404.html", path, pathArg, arg)){
			return true;
		}
		
		response.setStatus(404);
		return false;
	}
}

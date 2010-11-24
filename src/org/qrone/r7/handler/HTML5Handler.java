package org.qrone.r7.handler;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

public class HTML5Handler implements URIHandler, Extendable{
	private PortingService services;
	private URIResolver resolver;
	private HTML5Deck deck;
	private JSDeck vm;
	
	public HTML5Handler(PortingService services) {
		this.services = services;
		this.resolver = services.getURIResolver();
		deck = new HTML5Deck(resolver, services.getImageBufferService());
		vm = new JSDeck(resolver, deck);
	}
	
	public void addExtension(Class c){
		vm.addExtension(c);
		deck.addExtension(c);
	}

	@Override
	public boolean handle(HttpServletRequest request, HttpServletResponse response, 
			String path, String pathArg) {
		try {
			deck.update(new URI(path));
			response.setCharacterEncoding("utf8");
			if(resolver.exist(path + ".server.js")){
				URI uri = new URI(path + ".server.js");
				JSOM om = vm.compile(uri);
				if(om != null){
					Scriptable scope = vm.createScope();
					ServletScope ss = new ServletScope(
							request,response,path,pathArg,
							scope,deck,vm,uri,services);
					om.run(scope, new Window(ss));
					ss.writer.flush();
					ss.writer.close();
					return true;
				}
			}
			
			if(resolver.exist(path + ".html")){
				URI uri = new URI(path + ".html");
				HTML5OM om = deck.compile(uri);
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
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}

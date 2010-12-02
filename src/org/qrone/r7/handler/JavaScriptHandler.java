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

public class JavaScriptHandler implements URIHandler{
	private PortingService services;
	private HTML5Deck deck;
	private JSDeck vm;
	private URIResolver resolver;
	
	public JavaScriptHandler(PortingService services, JSDeck vm, HTML5Deck deck) {
		this.services = services;
		this.resolver = services.getURIResolver();
		this.deck = deck;
		this.vm = vm;
	}

	@Override
	public boolean handle(HttpServletRequest request, HttpServletResponse response, 
			String uri, String path, String pathArg) {
		try {
			if(resolver.exist(uri)){
				URI urio = new URI(uri);
				JSOM om = vm.compile(urio);
				if(om != null){
					Scriptable scope = vm.createScope();
					ServletScope ss = new ServletScope(
							request,response,path,pathArg,
							scope,deck,vm,urio,services);
					om.run(scope, new Window(ss));
					ss.writer.flush();
					ss.writer.close();
					return true;
				}
			}
		} catch (Exception e) {
			try{
				URI urio = new URI("/system/exception.server.js");
				Scriptable scope = vm.createScope();
				ServletScope ss = new ServletScope(
						request,response,path,pathArg,
						scope,deck,vm,urio,services);
				scope.put("exception", scope, e);
				JSOM om = vm.compile(urio);
				om.run(scope, new Window(ss));
			}catch (Exception e1){
				e1.printStackTrace();
			}
		}
		return false;
	}
}

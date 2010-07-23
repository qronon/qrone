package org.qrone.r7.handler;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.script.ScriptDeck;
import org.qrone.r7.script.ScriptOM;

public class JSHandler implements URIHandler{
	private URIResolver resolver;
	private ScriptDeck vm;
	
	public JSHandler(URIResolver resolver) {
		this.resolver = resolver;
		vm = new ScriptDeck(resolver);
	}

	@Override
	public boolean handle(HttpServletRequest request, HttpServletResponse response){
		try {
			String path = request.getPathInfo();
			if(resolver.exist(path + ".js")){
				path += ".js"; 
			}
			
			if(path.endsWith(".js") && resolver.exist(path)){
				URI uri = new URI(path);
				ScriptOM om = vm.compile(new URI(request.getServletPath()));
				if(om != null){
					om.run(request, response);
					return true;
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return false;
	}
}

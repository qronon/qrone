package org.qrone.r7.handler;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.Scriptable;
import org.qrone.img.ImageBufferService;
import org.qrone.r7.Extendable;
import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.HTML5OM;
import org.qrone.r7.parser.JSDeck;
import org.qrone.r7.parser.JSOM;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.script.ScriptablePrototype;
import org.qrone.r7.script.ServletScope;
import org.qrone.r7.script.browser.LoginService;
import org.qrone.r7.script.browser.Window;
import org.qrone.r7.tag.HTML5TagHandler;

public class HTML5Handler implements URIHandler, Extendable{
	private URIResolver resolver;
	private HTML5Deck deck;
	private JSDeck vm;
	private LoginService loginService;
	
	public HTML5Handler(URIResolver resolver, ImageBufferService service, LoginService loginService) {
		this.resolver = resolver;
		this.loginService = loginService;
		deck = new HTML5Deck(resolver, service);
		vm = new JSDeck(resolver, deck);
	}
	
	public void addExtension(Class c){
		if(ScriptablePrototype.class.isAssignableFrom(c)){
			vm.addExtension(c);
		}else if(HTML5TagHandler.class.isAssignableFrom(c)){
			deck.addExtension(c);
		}
	}

	@Override
	public boolean handle(HttpServletRequest request, HttpServletResponse response, 
			String path, String pathArg) {
		try {
			deck.update(new URI(path));
			response.setCharacterEncoding("utf8");
			if(resolver.exist(path + ".js")){
				URI uri = new URI(path + ".js");
				JSOM om = vm.compile(uri);
				if(om != null){
					Scriptable scope = vm.createScope();
					ServletScope ss = new ServletScope(
							request,response,path,pathArg,
							scope,deck,vm,uri,loginService);
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

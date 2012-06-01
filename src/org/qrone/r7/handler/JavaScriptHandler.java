package org.qrone.r7.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrappedException;
import org.qrone.r7.PortingService;
import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.JSDeck;
import org.qrone.r7.parser.JSOM;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.script.Scriptables;
import org.qrone.r7.script.ServletScope;
import org.qrone.r7.script.browser.User;
import org.qrone.r7.script.browser.Window;
import org.qrone.r7.script.ext.MapPrototype;
import org.qrone.util.QrONEUtils;
import org.qrone.util.Stream;

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
			String uri, String path, String leftpath) {
		Scriptable globalscope = null;
		try {
			if(resolver.exist(uri)){
				URI urio = new URI(uri);
				JSOM om = vm.compile(urio);
				if(om != null){
					
					
					globalscope = vm.createScope();
					ServletScope ss = new ServletScope(request, response, urio, path, leftpath);
					Window window = new Window(ss,globalscope,deck,vm,services);
					window.init(globalscope);
					

					if(uri.indexOf("/api/") >= 0 && !ss.secure){
						Map map = new HashMap();
						map.put("status", "error");
						map.put("code", "NO_TICKET");
						map.put("message", "Need ticket for accessing apis.");
						map.put("url", "http://...");
						Writer w = response.getWriter();
						w.write(JSON.encode(map));
						w.flush();
						w.close();
						return true;
					}
					
					
					
					Scriptable subscope = vm.createScope();
					JSOM defaultom = vm.compile(new URI("/system/resource/default.js"));
					defaultom.run(subscope);
					
					Object result = 
						om.run(globalscope, window, subscope, window);
					
					String done = ss.getParameter(".done");
					if(done != null && ss.secure){
						//String r = QrONEUtils.escape(JSON.encode(result));
						try {
							if(done.indexOf('?') >= 0){
								response.sendRedirect(done);
							}else{
								response.sendRedirect(done);
							}
						} catch (IOException e) {
						}
					}
					
					if(ss.getParameter(".debug") != null){
						JavaScriptException e =  new JavaScriptException(null, uri, 0);
						throw e;
					}
					
					window.close();
					
					return true;
				}
			}
		} catch (RhinoException e) {
			if(e instanceof WrappedException){
				((WrappedException)e).getWrappedException().printStackTrace();
			}else{
				e.printStackTrace();
			}
			
			try{
				response.setStatus(500);
				
				Map map = new HashMap();
				map.put("line", e.lineNumber());
				map.put("file", e.sourceName());
				map.put("message", e.getMessage());
				map.put("stacktrace", e.getScriptStackTrace());
				map.put("scope", JSON.encode(Scriptables.asMap(globalscope)));
				
				InputStream in = resolver.getInputStream(new URI(e.sourceName()));
				if(in != null){
					map.put("source", new String(Stream.read(in)));
				}
				
				URI urio = new URI("/admin/error.server.js");
				Scriptable scope = vm.createScope();
				ServletScope ss = new ServletScope(request,response,urio, path, leftpath);
				scope.put("exception", scope, new MapPrototype(scope, map));
				
				JSOM defaultom = vm.compile(new URI("/system/resource/default.js"));
				defaultom.run(scope);
				
				JSOM om = vm.compile(urio);
				Window window = new Window(ss,scope,deck,vm,services);
				window.init(scope);
				
				om.run(scope, window, window);
				window.close();
			}catch (Exception e1){
				e1.printStackTrace();
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}

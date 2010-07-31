package org.qrone.r7.handler;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.Scriptable;
import org.qrone.img.ImageBufferService;
import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.HTML5OM;
import org.qrone.r7.parser.JSDeck;
import org.qrone.r7.parser.JSOM;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.script.LocalWindow;
import org.qrone.r7.script.ServletScope;
import org.qrone.r7.script.Window;
import org.qrone.r7.tag.ImageHandler;
import org.qrone.r7.tag.Scale9Handler;

public class HTML5Handler implements URIHandler{
	private URIResolver resolver;
	private HTML5Deck deck;
	private JSDeck vm;
	private Object global;
	
	public HTML5Handler(URIResolver resolver, ImageBufferService service) {
		this.resolver = resolver;
		deck = new HTML5Deck(resolver, service);
		deck.addTagHandler(new Scale9Handler(deck));
    	deck.addTagHandler(new ImageHandler(deck));
		vm = new JSDeck(resolver, deck);
	}

	@Override
	public boolean handle(HttpServletRequest request, HttpServletResponse response) {
		long start = System.currentTimeMillis();
		
		response.setCharacterEncoding("utf8");
		try {
			String path = request.getPathInfo();
			deck.update(new URI(path));
			

			
			if(resolver.exist(path + ".js")){
				path += ".js"; 
			}
			
			if(path.endsWith(".js") && resolver.exist(path)){
				URI uri = new URI(path);
				JSOM om = vm.compile(uri);
				if(om != null){
					Scriptable scope = om.createScope();
					ServletScope ss = new ServletScope();
					ss.path = path;
					ss.uri = uri;
					ss.request = request;
					ss.response = response;
					ss.writer = response.getWriter();
					ss.scope = scope;
					ss.vm = vm;
					ss.deck = deck;
					ss.resolver = resolver;
					om.run(scope, new Window(ss), new LocalWindow(ss));
					
					ss.writer.append("<!-- execution time " + (System.currentTimeMillis()-start) + "ms -->");
					ss.writer.flush();
					ss.writer.close();
					return true;
				}
			}
			
			if(resolver.exist(path + ".html")){
				path += ".html"; 
			}
			
			if((path.endsWith(".html") || path.endsWith(".htm")) 
					&& resolver.exist(path)){
				URI uri = new URI(path);
				HTML5OM om = deck.compile(uri);
				if(om != null){
					deck.getSpriter().create();

					Writer out = response.getWriter();
					out.append(om.serialize());

					out.append("<!-- execution time " + (System.currentTimeMillis()-start) + "ms -->");
					out.flush();
					out.close();
					
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

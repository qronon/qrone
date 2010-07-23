package org.qrone.r7.handler;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.HTML5OM;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.tag.ImageHandler;
import org.qrone.r7.tag.Scale9Handler;

public class HTML5Handler implements URIHandler{
	private URIResolver resolver;
	private HTML5Deck deck;
	
	public HTML5Handler(URIResolver resolver) {
		this.resolver = resolver;
		deck = new HTML5Deck(resolver);
		deck.addTagHandler(new Scale9Handler(deck));
    	deck.addTagHandler(new ImageHandler(deck));
	}

	@Override
	public boolean handle(HttpServletRequest request, HttpServletResponse response) {
		try {
			String path = request.getPathInfo();
			if(resolver.exist(path + ".html")){
				path += ".html"; 
			}
			if(resolver.exist(path + ".htm")){
				path += ".htm"; 
			}
			
			if((path.endsWith(".html") || path.endsWith(".htm")) 
					&& resolver.exist(path)){
				URI uri = new URI(path);
				HTML5OM om = deck.compile(uri);
				if(om != null){
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

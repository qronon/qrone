package org.qrone.r7.handler;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.HTML5Element;
import org.qrone.r7.parser.HTML5OM;
import org.qrone.r7.parser.ImageSpriter;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

public class ImageHandler extends HTML5TagHandler {
	private HTML5Deck deck;
	public ImageHandler(HTML5Deck deck) {
		this.deck = deck;
	}
	
	@Override
	public HTML5TagResult process(HTML5Element e) {
		spriteTag(e.getOM().getURI(), e.get());
		return null;
	}

	public void spriteTag(URI file, Element e){
		try {
			String src = e.getAttribute("src");
			URI uri = file.resolve(src);
			if(e.getNodeName().equals("img") 
					&& src != null
					&& deck.getResolver().exist(uri)){
				
				String style = e.getAttribute("style");
				e.setAttribute("style", deck.getSpriter().addISprite(uri) + style);
				
				e.setAttribute("src", file.relativize(deck.getSpriter().addTransparentDot()).toString());
			}
		} catch (DOMException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}

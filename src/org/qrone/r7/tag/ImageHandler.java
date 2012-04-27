package org.qrone.r7.tag;

import java.io.IOException;
import java.net.URI;

import org.qrone.img.ImagePart;
import org.qrone.img.ImageSpriteService;
import org.qrone.r7.PortingService;
import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.HTML5Element;
import org.qrone.util.Net;
import org.qrone.util.QrONEUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

public class ImageHandler implements HTML5TagHandler {
	private ImageSpriteService service;
	private HTML5Deck deck;
	public ImageHandler(HTML5Deck deck) {
		this.deck = deck;
		
		PortingService port = deck.getPortingService();
		if(port != null){
			service = port.getImageSpriteService();
		}
	}
	
	@Override
	public HTML5TagResult process(HTML5Element e) {
		spriteTag(e.getOM().getURI(), e.get());
		return null;
	}

	public void spriteTag(URI file, Element e){
		try {
			if(service != null && e.getNodeName().toLowerCase().equals("img")){
				String src = e.getAttribute("src");
				URI uri = file.resolve(src);
				if(src != null
						&& deck.getResolver().exist(uri.toString())){
					
					String style = e.getAttribute("style");
					e.setAttribute("style", service.getStyle(new ImagePart(uri, service)) + style);
					
					e.setAttribute("src", Net.relativize(file,service.addTransparentDot()).toString());
				}
			}
		} catch (DOMException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}

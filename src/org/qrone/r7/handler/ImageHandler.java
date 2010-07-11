package org.qrone.r7.handler;

import java.io.File;
import java.io.IOException;

import org.qrone.r7.ImageSpriter;
import org.qrone.r7.XOM;
import org.qrone.r7.parser.HTML5Element;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

public class ImageHandler extends HTML5TagHandler {
	private XOM xom;
	public ImageHandler(XOM xom) {
		this.xom = xom;
	}
	
	@Override
	public HTML5TagResult process(HTML5Element e) {
		spriteTag(xom.getFile(), e.get());
		return null;
	}

	public void spriteTag(File file, Element e){
		try {
			String src = e.getAttribute("src");
			if(e.getNodeName().equals("img") 
					&& src != null
					&& new File(file.getParentFile(), src).exists()){
				
				String style = e.getAttribute("style");
				e.setAttribute("style", ImageSpriter.instance().addISprite(new File(file.getParentFile(), src)) + style);
				
				e.setAttribute("src", ImageSpriter.instance().addTransparentDot());
			}
		} catch (DOMException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}

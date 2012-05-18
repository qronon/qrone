package org.qrone.r7.tag;

import java.net.URI;
import java.net.URISyntaxException;

import org.qrone.r7.parser.CSS3OM;
import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.HTML5Element;
import org.w3c.dom.Element;

public class CSSClassIncluderHandler implements HTML5TagHandler {
	public CSSClassIncluderHandler(HTML5Deck deck) {
	}
	
	@Override
	public HTML5TagResult process(HTML5Element e) {
		String cls = e.get().getAttribute("class");
		int idx = cls.indexOf(":");
		if(idx > 0){
			String path = cls.substring(0, idx);
			e.attr("class", cls.substring(idx+1));
			
			try {
				CSS3OM css = e.getOM().getCSS3Deck().compile(new URI("/libs/css/" + path + ".css"));
				if(css != null)
					e.getOM().getStyleSheets().add(css);
			} catch (URISyntaxException e1) {}
		}
		return null;
	}
}

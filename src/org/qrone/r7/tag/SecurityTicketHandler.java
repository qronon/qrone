package org.qrone.r7.tag;

import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.HTML5Element;
import org.w3c.dom.Element;

public class SecurityTicketHandler implements HTML5TagHandler {
	public SecurityTicketHandler(HTML5Deck deck) {
	}
	
	@Override
	public HTML5TagResult process(HTML5Element e) {
		if(e.get().getNodeName().toLowerCase().equals("form")){
			return new HTML5TagResult() {
				
				@Override
				public String prestart(String ticket) {
					return null;
				}
				
				@Override
				public String preend(String ticket) {
					if(ticket != null)
						return "<input type=\"hidden\" name=\".ticket\" value=\"" + ticket + "\"/>";
					return null;
				}
				
				@Override
				public String poststart(String ticket) {
					return null;
				}
				
				@Override
				public String postend(String ticket) {
					return null;
				}
			};
		}
		return null;
	}
}

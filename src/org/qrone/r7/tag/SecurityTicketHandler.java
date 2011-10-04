package org.qrone.r7.tag;

import org.qrone.login.SecurityService;
import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.HTML5Element;

public class SecurityTicketHandler implements HTML5TagHandler {
	private SecurityService security;
	public SecurityTicketHandler(HTML5Deck deck) {
		this.security = deck.getPortingService().getSecurityService();
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

				@Override
				public void process(HTML5Element e, String ticket) {
					
				}
			};
		}
		return null;
	}
}

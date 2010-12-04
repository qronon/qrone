package org.qrone.r7.tag;

import java.io.IOException;
import java.net.URI;

import org.qrone.login.SecurityService;
import org.qrone.r7.Extension;
import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.HTML5Element;
import org.qrone.util.QrONEUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

@Extension
public class SecurityTicketHandler implements HTML5TagHandler {
	private SecurityService security;
	public SecurityTicketHandler(SecurityService security) {
		this.security = security;
	}
	
	@Override
	public HTML5TagResult process(HTML5Element e) {
		if(e.get().getNodeName().toLowerCase().equals("form")){
			return new HTML5TagResult() {
				
				@Override
				public String prestart() {
					return null;
				}
				
				@Override
				public String preend() {
					return "<input type=\"hidden\" value=\"" + security.getTicket() + "\"/>";
				}
				
				@Override
				public String poststart() {
					return null;
				}
				
				@Override
				public String postend() {
					return null;
				}
			};
		}
		return null;
	}
}

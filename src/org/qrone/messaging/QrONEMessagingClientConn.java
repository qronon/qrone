package org.qrone.messaging;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.arnx.jsonic.JSON;

import org.qrone.util.Token;
import org.qrone.xmlsocket.XMLSocket;
import org.qrone.xmlsocket.event.XMLSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class QrONEMessagingClientConn implements XMLSocketListener{
	private static final Logger logger = LoggerFactory.getLogger(QrONEMessagingClientConn.class);
	
	private QrONEMessagingServer server;
	private XMLSocket xmlsocket;

	private Map<String, Token> map = new HashMap<String, Token>();
	private Set<String> joined = new HashSet<String>();
	
	public QrONEMessagingClientConn(QrONEMessagingServer server, XMLSocket xmlsocket) {
		this.server = server;
		this.xmlsocket = xmlsocket;
	}
	
	public XMLSocket getSocket(){
		return xmlsocket;
	}

	@Override
	public void onConnect(boolean success) {
		logger.info("onConnect:" + success);
		
	}

	@Override
	public void onClose() {
		logger.info("onClose");
		for (Iterator<String> iter = joined.iterator(); iter.hasNext();) {
			server.left(this, iter.next());
		}
	}

	@Override
	public void onError(Exception e) {
		logger.info("onError:" + e);
		e.printStackTrace();
		
	}

	@Override
	public void onTimeout() {
		logger.info("onTimeout");
	}

	@Override
	public void onData(String data) {
		logger.info("onData:" + data);
		
		Map obj = JSON.decode(data, Map.class);
		try{
			if(obj.containsKey(".ping")){
				String pingid = (String)obj.get(".ping");
				Map ping = new HashMap();
				ping.put(".status", "sent");
				ping.put(".ping", pingid);
				xmlsocket.send(JSON.encode(ping));
			}
			
			if(obj.containsKey(".to")){
				String sendto = (String)obj.get(".to");
				if(server.canSend(sendto, map.get(sendto))){
					server.to(this, sendto, obj);
				}
			}else if(obj.containsKey(".join")){
				String jointo = (String)obj.get(".join");
				if(server.canReceive(jointo, Token.parse(obj.get(".ticket").toString()))){
					server.join(this, jointo );
					joined.add(jointo);				
				}
			}else if(obj.containsKey(".left")){
				String leftfrom = (String)obj.get(".left");
				server.left(this, leftfrom);
				joined.remove(leftfrom);				
			}
		}catch(ClassCastException e){
		}catch(NullPointerException e){}
			
	}

}

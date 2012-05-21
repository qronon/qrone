package org.qrone.messaging;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import net.arnx.jsonic.JSON;

import org.qrone.kvs.KeyValueStore;
import org.qrone.kvs.KeyValueStoreService;
import org.qrone.util.Token;
import org.qrone.xmlsocket.XMLSocket;
import org.qrone.xmlsocket.XMLSocketServer;
import org.qrone.xmlsocket.event.XMLSocketListener;
import org.qrone.xmlsocket.event.XMLSocketServerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class QrONEMessagingServer implements XMLSocketServerListener{
	private static final Logger logger = LoggerFactory.getLogger(QrONEMessagingServer.class);
	public static final int SERVER_PORT = 9699;
	
	private XMLSocketServer socketServer;
	private Map<String, Token> signmap
		= new HashMap<String, Token>();
	private Map<String, Set<QrONEMessagingClientConn>> map
		= new HashMap<String, Set<QrONEMessagingClientConn>>();

	private KeyValueStore kvs;
	private Token key;
	
	public QrONEMessagingServer(Token key){
		this.key = key;
		
		try {
			socketServer = new XMLSocketServer();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		socketServer.setEncoding("UTF-8");
		socketServer.addXMLSocketServerListener(this);
	}
	
	public void listen(int port){
		socketServer.open(SERVER_PORT);
	}

	@Override
	public void onOpen(boolean success) {
		logger.info("onOpen:" + success);
	}

	@Override
	public void onClose() {
		logger.info("onClose");
	}

	@Override
	public void onError(Exception e) {
		logger.info("onError:" + e);
		e.printStackTrace();
	}

	public void to(String target, Map obj){
		logger.info("to:" + target + ":" + obj);
		Set<QrONEMessagingClientConn> set = map.get(target);
		if(set != null){
			for (Iterator<QrONEMessagingClientConn> iter = set.iterator(); iter.hasNext();) {
				iter.next().getSocket().send(JSON.encode(obj));
			}
		}
	}
	
	public void to(QrONEMessagingClientConn conn, String target, Map obj){
		logger.info("to:" + target + ":" + obj);
		Set<QrONEMessagingClientConn> set = map.get(target);
		if(set != null){
			for (Iterator<QrONEMessagingClientConn> iter = set.iterator(); iter.hasNext();) {
				iter.next().getSocket().send(JSON.encode(obj));
			}
		}
	}

	public boolean join(QrONEMessagingClientConn conn, String target){
		logger.info("join:" + target);
		Set<QrONEMessagingClientConn> set = map.get(target);
		if(set == null){
			set = new HashSet<QrONEMessagingClientConn>();
			map.put(target, set);
		}
		
		set.add(conn);
		return true;
	}
	
	public boolean left(QrONEMessagingClientConn conn, String target){
		logger.info("join:" + target);
		Set<QrONEMessagingClientConn> set = map.get(target);
		if(set != null){
			if(set.size() <= 1){
				if(map.containsKey(target)){					
					map.remove(target);
					return true;
				}
			}else{
				return set.remove(conn);
			}
		}
		return false;
		
	}

	public boolean canReceive(String target, Token ticket){
		return true;
	}
	
	public boolean canSend(String target, Token ticket){
		return true;
	}

	@Override
	public void onNewClient(XMLSocket xmlsocket) {
		logger.info("onNewClient:" + xmlsocket.toString());
		QrONEMessagingClientConn conn = new QrONEMessagingClientConn(this, xmlsocket);
	}
		
	public static void main(String[] args){
		logger.info("main:");
		QrONEMessagingServer server = new QrONEMessagingServer(new Token());
		server.listen(SERVER_PORT);
	}

}

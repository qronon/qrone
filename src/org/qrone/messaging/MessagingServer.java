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

public class MessagingServer implements XMLSocketServerListener, MessagingService{
	private static final Logger logger = LoggerFactory.getLogger(MessagingServer.class);
	public static final int SERVER_PORT = 9699;
	
	private XMLSocketServer socketServer;
	private Map<String, Token> signmap
		= new HashMap<String, Token>();
	private Map<String, Set<MessagingClientConn>> map
		= new HashMap<String, Set<MessagingClientConn>>();
	private Set<MessagingService.Listener> listener = new HashSet<MessagingService.Listener>();

	private KeyValueStore kvs;
	//private Token key;
	private MessagingService service;
	
	public MessagingServer(){
		//this.key = key;
		this.service = service;
		
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
		Set<MessagingClientConn> set = map.get(target);
		if(set != null){
			for (Iterator<MessagingClientConn> iter = set.iterator(); iter.hasNext();) {
				iter.next().getSocket().send(JSON.encode(obj));
			}
		}
		for (MessagingService.Listener l : listener) {
			l.onData(target, obj);
		}
	}
	
	public void to(MessagingClientConn conn, String target, Map obj){
		logger.info("to:" + target + ":" + obj);
		Set<MessagingClientConn> set = map.get(target);
		if(set != null){
			for (Iterator<MessagingClientConn> iter = set.iterator(); iter.hasNext();) {
				iter.next().getSocket().send(JSON.encode(obj));
			}
		}
		for (MessagingService.Listener l : listener) {
			l.onData(target, obj);
		}
	}

	public boolean join(MessagingClientConn conn, String target){
		logger.info("join:" + target);
		for (MessagingService.Listener l : listener) {
			l.onJoin(conn, target);
		}
		
		Set<MessagingClientConn> set = map.get(target);
		if(set == null){
			set = new HashSet<MessagingClientConn>();
			map.put(target, set);
		}
		set.add(conn);
		return true;
		
	}
	
	public boolean left(MessagingClientConn conn, String target){
		logger.info("join:" + target);
		for (MessagingService.Listener l : listener) {
			l.onJoin(conn, target);
		}
		
		Set<MessagingClientConn> set = map.get(target);
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
		MessagingClientConn conn = new MessagingClientConn(this, xmlsocket);
	}
		
	public static void main(String[] args){
		logger.info("main:");
		MessagingServer server = new MessagingServer();
		server.listen(SERVER_PORT);
	}

	@Override
	public void addListener(Listener l) {
		listener.add(l);
	}

	@Override
	public void removeListener(Listener l) {
		listener.remove(l);
	}

}

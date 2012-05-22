package org.qrone.r7.script.browser;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

import org.mozilla.javascript.Scriptable;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageException;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchResponse;
import org.qrone.kvs.KeyValueStore;
import org.qrone.r7.PortingService;
import org.qrone.r7.script.Scriptables;
import org.qrone.r7.script.ext.ScriptableMap;
import org.qrone.util.QrONEUtils;
import org.qrone.util.Token;

public class User{

	private HttpServletRequest request;
	private HttpServletResponse response;
	private Token key;
	private KeyValueStore kvs;

	private String initialstore;
	private Map store;

	private Token qcookie = null;
	private Token ncookie = null;
	private Token bcookie = null;
	
	private PortingService service;
	
	public User(HttpServletRequest request, HttpServletResponse response, Token key, PortingService service){
		
		this.request = request;
		this.response = response;
		this.key = key;
		this.kvs = kvs;
		this.service = service;
		
		Cookie[] cookies = request.getCookies();
		if(cookies != null){
			for (int i = 0; i < cookies.length; i++) {
				if(cookies[i].getName().equals("Q")){
					Token q = Token.parse(cookies[i].getValue());
					if(q != null && q.validate("Q", key)){
						qcookie = q;
					}
				}else if(cookies[i].getName().equals("N")){
					Token n = Token.parse(cookies[i].getValue());
					if(n != null && n.validate("N", key)){
						ncookie = n;
					}
				}else if(cookies[i].getName().equals("B")){
					Token b = Token.parse(cookies[i].getValue());
					if(b != null && b.validate("B", key)){
						bcookie = b;
					}
				}
			}
		}
		
		KeyValueStore kvs = service.getKeyValueStoreService()
			.getKeyValueStore("qrone.user");
		if(qcookie != null){
			String name = qcookie.getId();
			initialstore = kvs.get("q." + name).toString();
			store = JSON.decode(initialstore);
		}else if(bcookie != null){
			String unique = bcookie.getId();
			initialstore = kvs.get("b." + unique).toString();
			store = JSON.decode(initialstore);
		}
		
		
		if(bcookie == null){
			bcookie = new Token(key,"B",Token.uniqueid());
			Cookie c = new Cookie("B", bcookie.toString());
			c.setMaxAge(60*60*24*256*20);
			c.setPath("/");
			response.addCookie(c);
		}
		
	}

	private void updateNCookie(Token nc){
		ncookie = nc;
        Cookie q = new Cookie("Q", nc.toString());
        q.setPath("/");
        response.addCookie(q);
	}
	
	private void updateQCookie(Token qc){
		qcookie = qc;
        Cookie q = new Cookie("Q", qc.toString());
        q.setPath("/");
        response.addCookie(q);
	}

	public String getId(){
		if(qcookie != null)
			return qcookie.getId();
		return null;
	}
	

	public String getName(){
		if(ncookie != null)
			return ncookie.getId();
		return null;
	}
	
	public String getDeviceId(){
		return bcookie.toString();
	}
	
	public void login(String id) {
		updateQCookie(new Token(key, "Q", "id:" + id));
	}
	
	public void guestLogin() {
		updateQCookie(new Token(key, "Q", "guest:" + QrONEUtils.uniqueid()));
	}
	
	public void openidLogin(Identifier verified, AuthSuccess authSuccess) {
        String name = null;
        if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)){
			try {
				FetchResponse fetchResp = (FetchResponse) authSuccess.getExtension(AxMessage.OPENID_NS_AX);
				name = fetchResp.getAttributeValue("login");
				updateNCookie(new Token(key, "N", name));
			} catch (MessageException e) {}
        }
        
        updateQCookie(new Token(key, "Q", "openid:" + verified.getIdentifier()));
	}
	
	public void logout() {
		qcookie = null;
        Cookie q = new Cookie("Q", "");
        q.setPath("/");
        response.addCookie(q);
	}
	
	public String getTicket(){
		User user = (User)request.getAttribute("User");
		return new Token(bcookie,"C",null).toString();
	}
	
	public boolean validateTicket(String ticket){
		Token t = Token.parse(ticket);
		if(t != null)
			return t.validate("C",bcookie);
		else
			return false;
	}
	
	public Object getStore(){
		return store;
	}
	
	public void setStore(Object s){
		if(s instanceof Scriptable){
			store = new ScriptableMap((Scriptable)s);
		}else if(s instanceof Map){
			store = (Map)s;
		}else{
			throw new IllegalArgumentException();
		}
	}

	public void close() {
		if(store != null && !store.equals(initialstore)){
			KeyValueStore kvs = service.getKeyValueStoreService()
				.getKeyValueStore("qrone.user");
			
			if(qcookie != null){
				String name = qcookie.getId();
				kvs.set("q." + name, JSON.encode(Scriptables.asMap(store)));
			}else if(bcookie != null){
				String unique = bcookie.getId();
				kvs.set("b." + unique, JSON.encode(Scriptables.asMap(store)));
			}
		}
	}


}

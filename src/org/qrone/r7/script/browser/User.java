package org.qrone.r7.script.browser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
import org.qrone.database.DatabaseCursor;
import org.qrone.database.DatabaseService;
import org.qrone.database.DatabaseTable;
import org.qrone.kvs.KeyValueStore;
import org.qrone.login.AccessToken;
import org.qrone.login.ID;
import org.qrone.r7.PortingService;
import org.qrone.r7.script.Scriptables;
import org.qrone.r7.script.ext.ScriptableMap;
import org.qrone.util.QrONEUtils;
import org.qrone.util.Token;

public class User{

	private HttpServletRequest request = null;
	private HttpServletResponse response = null;
	private PortingService service = null;
	
	private AccessToken userToken = null;
	private AccessToken browserToken = null;
	private Map<String, String> cookies = new HashMap<String, String>();
	
	private String initialstore = null;
	private Map store = null;
	private boolean opened = false;
	
	private DatabaseService db;
	
	public User(HttpServletRequest request, HttpServletResponse response, PortingService service){
		
		this.request = request;
		this.response = response;
		this.service = service;
		
		UUID key = service.getConsumerSecret();
		
		Cookie[] ck = request.getCookies();
		if(cookies != null){
			for (int i = 0; i < ck.length; i++) {
				cookies.put(ck[i].getName(), ck[i].getValue());
			}
		}

		AccessToken rcookie = AccessToken.parse(cookies.get(AccessToken.READ));
		if(rcookie != null && rcookie.validate(key, AccessToken.READ)){
			if(userToken == null){
				userToken = rcookie;
			}
		}
		
		browserToken = AccessToken.parse(cookies.get(AccessToken.BROWSER));
		if(browserToken == null || !browserToken.validate(key, AccessToken.BROWSER)){
			browserToken = new AccessToken(ID.encryptOpenID(UUID.randomUUID(),service.getConsumerID(),key), AccessToken.BROWSER);
			browserToken.sign(key);
			Cookie bck = new Cookie(AccessToken.BROWSER, browserToken.toString());
			bck.setMaxAge(60*60*24*256*20);
			bck.setPath("/");
			response.addCookie(bck);
		}
		
		if(userToken == null){
			userToken = browserToken;
		}
		
	}

	public boolean validateTicket(String pt) {
		return validateTicket(pt, AccessToken.WRITE);
	}
	
	public boolean validateTicket(String pt, String permittion) {
		if(pt != null){
			AccessToken ticket = AccessToken.parse(pt);
			if(ticket != null && ticket.validate(service.getConsumerSecret(), permittion)){
				return true;
			}
		}
		return false;
	}
	
	public UUID getUUID(){
		if(userToken != null)
			return ID.decryptOpenID(userToken.getId(), service.getConsumerSecret());
		return null;
	}
	
	public String getId(){
		if(userToken != null)
			return getUUID().toString();
		return null;
	}
	
	public String getBrowserId(){
		if(userToken != null)
			return ID.decryptOpenID(browserToken.getId(), service.getConsumerSecret()).toString();
		return null;
	}

	public String getTicket(){
		return getTicket(AccessToken.WRITE);
	}
	
	public String getTicket(String permittion){
		AccessToken ticket = new AccessToken(ID.encryptOpenID(getUUID(),service.getConsumerID(),service.getConsumerSecret()), permittion);
		ticket.sign(service.getConsumerSecret());
		return ticket.toString();
	}

	public void openidLogin(Identifier verified, AuthSuccess authSuccess) {
		// TODO Not implemented yet.
		
        String name = null;
        if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)){
			try {
				FetchResponse fetchResp = (FetchResponse) authSuccess.getExtension(AxMessage.OPENID_NS_AX);
				name = fetchResp.getAttributeValue("login");
				//updateNCookie(new Token(key, "N", name));
			} catch (MessageException e) {}
        }
        
        //updateQCookie(new Token(key, "Q", "openid:" + verified.getIdentifier()));
	}
	
	public void login(String id){
		userToken = new AccessToken(ID.encryptOpenID(UUID.fromString(id),service.getConsumerID(),service.getConsumerSecret()), AccessToken.READ);
		userToken.sign(service.getConsumerSecret());
		Cookie uck = new Cookie(AccessToken.READ, userToken.toString());
		uck.setMaxAge(60*60*24*256*20);
		uck.setPath("/");
		response.addCookie(uck);
	}
	
	public void logout(){
		Cookie uck = new Cookie(AccessToken.READ, "");
		uck.setMaxAge(0);
		uck.setPath("/");
		userToken = browserToken;
		response.addCookie(uck);
	}	
		
	public Object getStore(){
		if(!opened){
			opened = true;
			
			KeyValueStore kvs = service.getKeyValueStoreService().getKeyValueStore("qrone.user");
			initialstore = (String)kvs.get("l." + getId());
			if(initialstore != null){
				store = JSON.decode(initialstore);
			}else{
				store = new HashMap();
			}
		}
		return store;
	}	
		
	public void setStore(Object s){
		opened = true;
		if(s instanceof Scriptable){
			store = Scriptables.asMap(s);
		}else if(s instanceof Map){
			store = (Map)s;
		}else{
			throw new IllegalArgumentException();
		}
	}
	
	public void close() {
		if(store != null && opened){
			String currentstore = JSON.encode(store);
			if(!currentstore.equals(initialstore)){
				KeyValueStore kvs = service.getKeyValueStoreService().getKeyValueStore("qrone.user");
				kvs.set("l." + getId(), currentstore);
			}
		}
	}
	
}

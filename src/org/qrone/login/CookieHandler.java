package org.qrone.login;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthSuccess;
import org.qrone.kvs.KeyValueStore;
import org.qrone.kvs.KeyValueStoreService;
import org.qrone.r7.handler.URIHandler;
import org.qrone.r7.script.browser.User;
import org.qrone.util.QrONEUtils;
import org.qrone.util.Token;


public class CookieHandler implements URIHandler{

	private KeyValueStore kvs;
	private Token key;
	
	public CookieHandler(KeyValueStoreService service){
		this.kvs = service.getKeyValueStore("qrone.setting");
		if(key == null){
			byte[] keybytes = kvs.get("secretkey");
			if(keybytes == null){
				key = Token.generate("M",null);
				kvs.set("secretkey", key.getBytes());
			}else{
				key = new Token();
			}
		}
	}
	

	@Override
	public boolean handle(HttpServletRequest request,
			HttpServletResponse response, String uri, String path, String pathArg) {
		request.setAttribute("User", new User(request, response, key));
		return false;
	}

}

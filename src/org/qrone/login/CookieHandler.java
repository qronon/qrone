package org.qrone.login;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthSuccess;
import org.qrone.kvs.KeyValueStore;
import org.qrone.kvs.KeyValueStoreService;
import org.qrone.r7.PortingService;
import org.qrone.r7.handler.URIHandler;
import org.qrone.r7.script.browser.User;
import org.qrone.util.QrONEUtils;
import org.qrone.util.Token;


public class CookieHandler implements URIHandler{

	private PortingService service;
	
	public CookieHandler(PortingService service){
		this.service = service;
	}
	

	@Override
	public boolean handle(HttpServletRequest request,
			HttpServletResponse response, String uri, String path, String pathArg, List<String> arg) {
		request.setAttribute("User", new User(request, response, service));
		return false;
	}

}

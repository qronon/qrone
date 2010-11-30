package org.qrone.login;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.kvs.KeyValueStore;
import org.qrone.kvs.KeyValueStoreService;
import org.qrone.r7.handler.URIHandler;
import org.qrone.util.QrONEUtils;


public class CookieHandler implements URIHandler, SecurityService{
	private static byte[] key;
	private KeyValueStore kvs;
	
	public CookieHandler(KeyValueStoreService service){
		this.kvs = service.getKeyValueStore("qrone.setting");
		if(key == null){
			key = kvs.get("secretkey");
			if(key == null){
				key = QrONEUtils.generateKey();
				kvs.set("secretkey", key);
			}
		}
	}
	
	private String q,t,b;
	private byte[] qd,td,bd;

	@Override
	public boolean handle(HttpServletRequest request,
			HttpServletResponse response, String path, String pathArg) {
		
		Cookie[] cookies = request.getCookies();
		if(cookies != null){
			for (int i = 0; i < cookies.length; i++) {
				if(cookies[i].getName().equals("Q")){
					q = cookies[i].getValue();
					if(q != null)
						qd = QrONEUtils.base64_decode(q);
				}else if(cookies[i].getName().equals("T")){
					t = cookies[i].getValue();
					if(t != null)
						td = QrONEUtils.decrypt(QrONEUtils.base64_decode(t), key);
				}else if(cookies[i].getName().equals("B")){
					b = cookies[i].getValue();
					if(b != null)
						bd = QrONEUtils.decrypt(QrONEUtils.base64_decode(b), key);
				}
			}
		}

		if(qd == null || td == null || Arrays.equals(qd, td)){
			q = null;
			qd = null;
		}
		
		if(bd == null){
			bd = (String.valueOf(Calendar.getInstance(Locale.ENGLISH).getTimeInMillis())
					+ String.valueOf(Math.random())).getBytes();
			b = QrONEUtils.base64_encode(QrONEUtils.encrypt(bd, key));
			Cookie c = new Cookie("B", b);
			c.setMaxAge(0);
			c.setPath("/");
			response.addCookie(c);
			
		}
		
		
		return false;
	}
	
	public String getTicket(){
		return QrONEUtils.base64_encode(QrONEUtils.encrypt(bd, key));
	}
	
	public boolean validateTicket(String ticket){
		byte[] bdticket = QrONEUtils.decrypt(QrONEUtils.base64_decode(ticket),key);
		if(bdticket != null && Arrays.equals(bd, bdticket)){
			return true;
		}
		return false;
	}

	@Override
	public boolean isSecured() {
		// TODO Auto-generated method stub
		return false;
	}

}

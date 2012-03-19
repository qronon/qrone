package org.qrone.login;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
	

	@Override
	public boolean handle(HttpServletRequest request,
			HttpServletResponse response, String uri, String path, String pathArg) {

		String q,t,b;
		byte[] qd = null,td = null,bd = null;
		
		Cookie[] cookies = request.getCookies();
		if(cookies != null){
			for (int i = 0; i < cookies.length; i++) {
				if(cookies[i].getName().equals("Q")){
					q = cookies[i].getValue();
					if(q != null){
						qd = QrONEUtils.decodeQ64(q);
						request.setAttribute("QCookie", qd);
					}
				}else if(cookies[i].getName().equals("T")){
					t = cookies[i].getValue();
					if(t != null){
						td = QrONEUtils.decrypt(QrONEUtils.decodeQ64(t), key);
						request.setAttribute("TCookie", td);
					}
				}else if(cookies[i].getName().equals("B")){
					b = cookies[i].getValue();
					if(b != null){
						bd = QrONEUtils.decrypt(QrONEUtils.decodeQ64(b), key);
						if(bd != null && bd[0] == 'B' && bd[1] == 'C' && bd[2] == 'K'){
							request.setAttribute("BCookie", bd);
						}else{
							bd = null;
						}
					}
				}
			}
		}

		if(qd == null || td == null || Arrays.equals(qd, td)){
			q = null;
			qd = null;
		}
		
		if(bd == null){
			try {
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(bytes);
				out.writeByte('B');
				out.writeByte('C');
				out.writeByte('K');
				out.writeLong(Calendar.getInstance(Locale.ENGLISH).getTimeInMillis());
				out.writeDouble(Math.random());
				
				bd = bytes.toByteArray();
				b = QrONEUtils.encodeQ64(QrONEUtils.encrypt(bd, key));
				Cookie c = new Cookie("B", b);
				c.setMaxAge(60*60*24*256*20);
				c.setPath("/");
				response.addCookie(c);
				request.setAttribute("BCookie", bd);
				
			} catch (IOException e) {}
			
		}
		
		
		return false;
	}
	
	public String getTicket(HttpServletRequest request){
		return QrONEUtils.encodeQ64(QrONEUtils.encrypt((byte[])request.getAttribute("BCookie"), key));
	}
	
	public boolean validateTicket(HttpServletRequest request,String ticket){
		byte[] bdticket = QrONEUtils.decrypt(QrONEUtils.decodeQ64(ticket),key);
		if(bdticket != null && Arrays.equals((byte[])request.getAttribute("BCookie"), bdticket)){
			return true;
		}
		return false;
	}

}

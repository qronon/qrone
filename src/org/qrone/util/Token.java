package org.qrone.util;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

public class Token {
	private String type;
	private String id;
	private long timestamp;
	private double rand;
	private byte[] md5sign;

	public Token(){
		this(null, "X", "X");
	}
	
	public Token(Token secret, String type, String id){
		this.type = type;
		if(id != null)
			this.id = id;
		else
			this.id = "X";
		timestamp = System.currentTimeMillis();
		rand = Math.random();
		if(secret != null){
			md5sign = calcSign(secret);
		}else{
			md5sign = new byte[1];
			md5sign[0] = '0';
		}
	}
	
	public static Token parse(byte[] token){
		return parse(new String(token));
	}
	
	public static Token parse(String tokenString){
		Token t = new Token();
		try{
			String[] s = tokenString.split("\\-");
			t.type = QrONEUtils.hex2str(s[0]);
			t.id = QrONEUtils.hex2str(s[1]);
			t.timestamp = QrONEUtils.hex2long(s[2]);
			t.rand = QrONEUtils.hex2long(s[3]);
			if(s.length >= 4){
				t.md5sign = QrONEUtils.hex2bytearray(s[4]);
			}
			return t;
		}catch(Exception e){}
		return null;
	}
	
	public boolean isAnonymous(){
		return id.equals("X");
	}

	public String getType(){
		return type;
	}
	
	public String getId(){
		return id;
	}
	
	public long getTimestamp(){
		return timestamp;
	}

	public boolean validate(String type, Token sign, long millis){
		if(validate(type, sign) && (System.currentTimeMillis() - timestamp) < millis){
			return true;
		}
		return false;
	}
	
	public boolean validate(String type, Token sign){
		if(this.type.equals(type) && Arrays.equals(calcSign(sign), md5sign)){
			return true;
		}
		return false;
	}

	private byte[] calcSign(Token secret){
		try {
			return QrONEUtils.digest("MD5", (toBodyString() + secret.toString()).getBytes());
		} catch (NoSuchAlgorithmException e) {
			byte[] b = new byte[1];
			b[0] = '0';
			return b;
		}
	}

	private String toBodyString(){
		return  QrONEUtils.str2hex(type)
				+ "-" + QrONEUtils.str2hex(id)
				+ "-" + QrONEUtils.long2hex(timestamp)
				+ "-" + QrONEUtils.double2hex(rand);
	}
	
	public String toString(){
		return  toBodyString()
				+ "-" + QrONEUtils.bytearray2hex(md5sign);
	}
	
	public byte[] getBytes(){
		return toString().getBytes();
	}
	
	public static Token generate(String type, String id){
		return new Token(null, type, id);
	}

}

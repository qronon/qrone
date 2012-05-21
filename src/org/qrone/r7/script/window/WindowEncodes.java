package org.qrone.r7.script.window;

import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.URLCodec;
import org.mozilla.javascript.Scriptable;
import org.qrone.r7.script.browser.Window;
import org.qrone.util.Digest;

public class WindowEncodes implements WindowPrototype {
	
	public WindowEncodes( Window win ){
		
	}

	@Override
	public void init(Scriptable scr) {
		
	}
	
	public byte[] base64_decode(String base64String){
		return Base64.decodeBase64(base64String);
	}
	
	public String base64_encode(byte[] binaryData){
		return Base64.encodeBase64String(binaryData);
	}

	public byte[] base64_urlsafe_decode(String base64String){
		return Base64.decodeBase64(base64String);
	}

	public String base64_urlsafe_encode(byte[] binaryData){
		return Base64.encodeBase64URLSafeString(binaryData);
	}

	public String unescape(String str) throws DecoderException{
		URLCodec c = new URLCodec();
		return c.decode(str);
	}
	
	public String escape(String str) throws EncoderException{
		URLCodec c = new URLCodec();
		return c.encode(str);
	}

	public String md2(String data){
		return digest_safe("MD2", data);
	}
	
	public String md5(String data){
		return digest_safe("MD5", data);
	}
	
	public String sha1(String data){
		return digest_safe("SHA-1", data);
	}

	public String sha256(String data){
		return digest_safe("SHA-256", data);
	}

	public String sha384(String data){
		return digest_safe("SHA-384", data);
	}
	
	public String sha512(String data){
		return digest_safe("SHA-512", data);
	}
	
	private String digest_safe(String type, String data){
		try {
			return digest(type, data);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String digest(String type, String data) throws NoSuchAlgorithmException{
		return Digest.digest_hex(type, data);
	}

}

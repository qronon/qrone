package org.qrone.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;

import org.apache.commons.codec.binary.Base64;

import com.ibm.icu.text.CharsetDetector;

public class QrONEUtils{

    public static String getString(InputStream in, String contentType) throws IOException{
    	return getString(Stream.read(in), contentType);
    }
    
    public static String getString(byte[] bytes, String contentType){
    	String encoding = null;
    	if(contentType != null){
    		int idx = contentType.indexOf("charset=");
			if(idx >= 0){
				encoding = contentType.substring(idx + "charset=".length());
				
			}
    	}
    	
    	CharsetDetector cd = new CharsetDetector();
		return cd.getString(bytes, encoding);
    }
    
	private static int uniquekey = 0;
	public static String uniqueid(){
		return "qid" + (++uniquekey);
	}

	public static InputStream getResourceAsStream(String name, ServletContext c) throws IOException {
		InputStream in;
		if(c != null){
			in = QrONEUtils.class.getClassLoader().getResourceAsStream("org/qrone/r7/resource/" + name);
			if(in != null){
				return in;
			}
		}
		
		in = QrONEUtils.class.getClassLoader().getResourceAsStream("../r7/resource/" + name);
		if(in != null){
			return in;
		}
		
		in = new FileInputStream("src/org/qrone/r7/resource/" + name);
		return in;
	}
	
	public static String getContent(File file, String x) throws IOException{
		File s = new File(file, x);
		if(s.exists()){
			return new String(Stream.read(new FileInputStream(s)),"utf8");
		}
		return null;
	}
	
	public static String getResource(String name) throws IOException {
		InputStream in = QrONEUtils.class.getResourceAsStream("resource/" + name);
		if(in != null){
			return new String(Stream.read(in),"utf8");
		}
		
		in = new FileInputStream("src/org/qrone/r7/resource/" + name);
		return new String(Stream.read(in),"utf8");
	}

	public static byte[] base64_decode(String base64String){
		return Base64.decodeBase64(base64String);
	}
	
	public static String base64_encode(byte[] binaryData){
		return Base64.encodeBase64String(binaryData);
	}

	public static String base64_urlsafe_encode(byte[] binaryData){
		return Base64.encodeBase64URLSafeString(binaryData);
	}

	public static String escape(String str){
		if(str == null) return null;
		StringBuffer b = new StringBuffer();
		char[] ch = str.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			switch (ch[i]) {
			case '\t':
			case '\r':
			case '\n':
			case ' ':
				b.append(' ');
				break;
			case '<':
				b.append("&lt;");
				break;
			case '>':
				b.append("&gt;");
				break;
			case '"':
				b.append("&quot;");
				break;
			case '&':
				b.append("&amp;");
				break;
			case '\u00A0':
				b.append("&nbsp;");
				break;
			case '\0':
				break;
			default:
				b.append(ch[i]);
				break;
			}
		}
		return b.toString();
	}

	
	public static String packEQ64(Externalizable object){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Serialization.extenalize(object,out);
		return base64_urlsafe_encode(out.toByteArray());
	}

	public static Object unpackEQ64(Class c, String packed){
		ByteArrayInputStream in = new ByteArrayInputStream(base64_decode(packed));
		return Serialization.unextenalize(c, in);
	}
	
	public static String packQ64(Object object){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Serialization.serialize(object,out);
		return base64_urlsafe_encode(out.toByteArray());
	}
	
	public static Object unpackQ64(String packed){
		ByteArrayInputStream in = new ByteArrayInputStream(base64_decode(packed));
		return Serialization.unserialize(in);
	}
	
	public static Cookie getCookie(Cookie[] cookies, String name){
		if(cookies != null){
			for (int i = 0; i < cookies.length; i++) {
				if(cookies[i].getName().equals(name)){
					return cookies[i];
				}
			}
		}
		return null;
	}
	
	public static Date now(){
		return Calendar.getInstance(Locale.ENGLISH).getTime();
	}

	public static String toGMTString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(
				"E, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH );
		return sdf.format(date);
	}
	
	public static byte[] generateKey(){
		KeyGenerator keyGenerator;
		try {
			keyGenerator = KeyGenerator.getInstance("Blowfish");
			keyGenerator.init(128);
			return keyGenerator.generateKey().getEncoded();
		} catch (NoSuchAlgorithmException e) {}
		return null;
	}
	

	public static byte[] encrypt(byte[] data, byte[] key){
		if(data == null) return null;
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "Blowfish"));
			return cipher.doFinal(data);
		} catch (NoSuchAlgorithmException e) {
		} catch (NoSuchPaddingException e) {
		} catch (InvalidKeyException e) {
		} catch (IllegalBlockSizeException e) {
		} catch (BadPaddingException e) {
		}
		return null;
	}

	public static byte[] decrypt(byte[] data, byte[] key){
		if(data == null) return null;
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "Blowfish"));
			return cipher.doFinal(data);
		} catch (NoSuchAlgorithmException e) {
		} catch (NoSuchPaddingException e) {
		} catch (InvalidKeyException e) {
		} catch (IllegalBlockSizeException e) {
		} catch (BadPaddingException e) {
		}
		return null;
	}
	
}

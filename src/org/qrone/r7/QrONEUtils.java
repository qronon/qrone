package org.qrone.r7;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.Cookie;

import org.apache.commons.codec.binary.Base64;

public class QrONEUtils{

    public static void main(String[] args) {
    	String[] a = {"-v", "-n", "site/test"};
    	/*
    	try{
    		URI from = new URI("file.html/");
    		URI to = new URI("to/../path/to/../path/to/file.png");
    		System.out.println(from.resolve(to));
    		//System.out.println(relativize(from.toString(), to.toString()));
    		//from.relativize(from)
    	}catch(Exception e){
    		e.printStackTrace();
    	}*/
    	
    	QrONECompressor.main(a);
    	QrONECompressor.main(a);
	}

    public static URI relativize(URI basePath, URI targetPathString) {
    	String uri = relativize(basePath.toString(), targetPathString.toString());
    	try {
			return new URI(uri);
		} catch (URISyntaxException e) {
			return targetPathString;
		}
    }
    
    public static String relativize(String basePath, String targetPathString) {
        // We modify targetPath to become the result.
		StringBuilder targetPath = new StringBuilder(targetPathString);
	
		// Find the longest common initial sequence of path elements.
		int length = Math.min(basePath.length(), targetPath.length());
		int diff = 0;
		for (int i = 0; i < length; i++) {
		    char c = basePath.charAt(i);
		    if (c != targetPath.charAt(i))
			break;
		    if (c == '/')
			diff = i + 1;
		}
	
		// Remove the common initial elements from the target, including
		// their trailing slashes.
		targetPath.delete(0, diff);
	
		// Count remaining complete path elements in the base,
		// prefixing the target with "../" for each one.
		for (int slash = basePath.indexOf('/', diff); slash > -1;
		     slash = basePath.indexOf('/', slash + 1))
		    targetPath.insert(0, "../");
	
		// Make sure the result is not empty.
		if (targetPath.length() == 0)
		    targetPath.append("./");

        return targetPath.toString();
    }
    
	private static int uniquekey = 0;
	public static String uniqueid(){
		return "qid" + (++uniquekey);
	}

	public static InputStream getResourceAsStream(String name) throws IOException {
		InputStream in = QrONEUtils.class.getResourceAsStream("resource/" + name);
		if(in != null){
			return in;
		}
		
		in = new FileInputStream("src/org/qrone/r7/resource/" + name);
		return in;
	}
	
	public static String getContent(File file, String x) throws IOException{
		File s = new File(file, x);
		if(s.exists()){
			return QrONEUtils.convertStreamToString(new FileInputStream(s));
		}
		return null;
	}
	
	public static String getResource(String name) throws IOException {
		InputStream in = QrONEUtils.class.getResourceAsStream("resource/" + name);
		if(in != null){
			return convertStreamToString(in);
		}
		
		in = new FileInputStream("src/org/qrone/r7/resource/" + name);
		return convertStreamToString(in);
	}
	
	public static String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                is.close();
            }
            return sb.toString();
        } else {        
            return "";
        }
    }

	public static String escape(String str){
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

	public static void copy(InputStream in, OutputStream out) throws IOException {
		if(in == null || out == null) throw new IOException();
		int buf;
		while ((buf = in.read()) != -1) {
			out.write(buf);
		}
	}

	public static String encodeQ64(byte[] bytes){
		String base64String = Base64.encodeBase64String(bytes);
		base64String = base64String.replace('+', '.');
		base64String = base64String.replace('/', '_');
		base64String = base64String.replaceAll("\n", "");
		
		int i = base64String.indexOf('=');
		if(i >= 0)
			return base64String.substring(0, base64String.indexOf('='));
		return base64String;
	}
	
	public static byte[] decodeQ64(String base64String){
		base64String = base64String.replace('+', '.');
		base64String = base64String.replace('/', '_');
		
		int eq = 4 - base64String.length() % 4;
		switch(eq){
		case 1:
			return Base64.decodeBase64(base64String + "===");
		case 2:
			return Base64.decodeBase64(base64String + "==");
		case 3:
			return Base64.decodeBase64(base64String + "=");
		case 4:
			return Base64.decodeBase64(base64String);
		}
		return Base64.decodeBase64(base64String);
	}

	public static String packEQ64(Externalizable object){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		extenalize(object,out);
		return encodeQ64(out.toByteArray());
	}

	public static Object unpackEQ64(Class c, String packed){
		ByteArrayInputStream in = new ByteArrayInputStream(decodeQ64(packed));
		return unextenalize(c, in);
	}
	
	public static String packQ64(Object object){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		serialize(object,out);
		return encodeQ64(out.toByteArray());
	}
	
	public static Object unpackQ64(String packed){
		ByteArrayInputStream in = new ByteArrayInputStream(decodeQ64(packed));
		return unserialize(in);
	}
	

	public static byte[] serialize(Object o){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		serialize(o,out);
		return out.toByteArray();
	}
	
	public static boolean serialize(Object o, OutputStream out){
		ObjectOutputStream oout = null;
		try {
			oout = new ObjectOutputStream(out);
			oout.writeObject(o);
			oout.flush();
			return true;
		} catch (InvalidClassException e) {
		} catch (NotSerializableException e) {
		} catch (IOException e) {
		} finally {
			if (oout != null) {
				try {
					oout.close();
				} catch (IOException e) {
				}
			}
		}
		return false;
	}

	public static Object unserialize(byte[] bytes){
		return unserialize(new ByteArrayInputStream(bytes));
	}
	
	public static Object unserialize(InputStream in){
		ObjectInputStream oin = null;
		try {
			if (in != null) {
				oin = new ObjectInputStream(in);
				return oin.readObject();
			}
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		} finally {
			if (oin != null) {
				try {
					oin.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	public static boolean extenalize(Externalizable o, OutputStream out){
		ObjectOutputStream oout = null;
		try {
			oout = new ObjectOutputStream(out);
			o.writeExternal(oout);
			oout.flush();
			return true;
		} catch (InvalidClassException e) {
		} catch (NotSerializableException e) {
		} catch (IOException e) {
		} finally {
			if (oout != null) {
				try {
					oout.close();
				} catch (IOException e) {
				}
			}
		}
		return false;
	}
	
	public static Object unextenalize(Class c, InputStream in){
		ObjectInputStream oin = null;
		try {
			if (in != null) {
				oin = new ObjectInputStream(in);
				Externalizable r = (Externalizable)c.getConstructor().newInstance();
				r.readExternal(oin);
				return r;
			}
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		} catch (IllegalArgumentException e) {
		} catch (SecurityException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		} catch (NoSuchMethodException e) {
		} finally {
			if (oin != null) {
				try {
					oin.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
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
	
}

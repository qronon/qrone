package org.qrone.r7.script;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.BitSet;
import java.util.Properties;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.URLCodec;
import org.qrone.kvs.KVSService;
import org.qrone.kvs.MongoService;
import org.qrone.r7.parser.JSOM;

import com.mongodb.MongoException;


public class Window extends JSObject{
	public PrintStream in = System.out;
	public PrintStream out = System.out;
	public Document document;
	
	public Window(ServletScope ss) throws IOException{
		super(ss);
		document = new Document(ss);
	}

	public void require_once(String path) throws IOException, URISyntaxException{
		JSOM om = ss.vm.compile(new URI(path));
		if(!ss.required.contains(om)){
			ss.required.add(om);
			om.run(ss.scope);
		}
	}
	
	public void require(String path) throws IOException, URISyntaxException{
		JSOM om = ss.vm.compile(new URI(path));
		if(!ss.required.contains(om)){
			ss.required.add(om);
		}
		om.run(ss.scope);
	}

	public KVSService mongo_connect(String host, String schema)
			throws UnknownHostException, MongoException{
        return new MongoService(ss, host, schema);
	}
	
	public KVSService mongo_connect(String host, String schema, 
			String user, String password) throws UnknownHostException, MongoException{
        return new MongoService(ss, host, schema, user, password);
	}

	public Connection jdbc_connect(String cls, String url) throws SQLException{
		try {
			Class.forName(cls).newInstance();
		} catch (Exception e) {
			return null;
		}
		
		return DriverManager.getConnection(url);
	}
	
	public Connection jdbc_connect(String cls, String url, Properties info) throws SQLException{
		try {
			Class.forName(cls).newInstance();
		} catch (Exception e) {
			return null;
		}
		
		return DriverManager.getConnection(url, info);
	}
	
	public Connection derby_connect(String file, 
			String user, String password) throws SQLException{
		Properties props = new Properties();
        props.put("user", user);
        props.put("password", password);
        
		return jdbc_connect("org.apache.derby.jdbc.EmbeddedDriver", 
				"jdbc:derby:" + file + ";create=true", props);
	}
	
	public Connection mysql_connect(String host, String schema, 
			String user, String password) throws SQLException{
		Properties props = new Properties();		
        props.put("user", user);		
        props.put("password", password);	
        props.put("useUnicode", "true");	
        props.put("characterEncoding", "utf8");		

		return jdbc_connect("com.mysql.jdbc.Driver", 
				"jdbc:mysql://" + host + "/" + schema, props);
	}
	
	public byte[] base64_decode(String base64String){
		return Base64.decodeBase64(base64String);
	}
	
	public String base64_encode(byte[] binaryData){
		return Base64.encodeBase64String(binaryData);
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
		MessageDigest md = MessageDigest.getInstance(type);
		byte[] digest = data.getBytes();
		md.update(digest);

		StringBuffer b = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
			int d = digest[i];
			if (d < 0) {
				d += 256;
			}
			if (d < 16) {
				b.append('0');
			}
			b.append(Integer.toString(d, 16));
		}
		return b.toString();
	}

}

package org.qrone.r7.script.browser;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.URLCodec;
import org.mozilla.javascript.Scriptable;
import org.qrone.database.DatabaseService;
import org.qrone.deck.PropertiesDeck;
import org.qrone.deck.TextileDeck;
import org.qrone.deck.YamlDeck;
import org.qrone.memcached.MemcachedService;
import org.qrone.r7.RepositoryService;
import org.qrone.r7.parser.HTML5OM;
import org.qrone.r7.parser.HTML5Template;
import org.qrone.r7.parser.JSOM;
import org.qrone.r7.script.ServletScope;
import org.qrone.r7.script.ServletScopeObject;
import org.qrone.util.QrONEUtils;


public class Window extends ServletScopeObject{
	public PrintStream in = System.out;
	public PrintStream out = System.out;
	public Document document;
	public Location location;
	public Navigator navigator;
	public Object query;
	public JSON JSON;
	public DatabaseService db;
	public MemcachedService memcached;
	public RepositoryService repository;
	
	public Window(ServletScope ss) throws IOException, URISyntaxException{
		super(ss);
		document = new Document(ss);
		
		location = new Location(ss);
		navigator = new Navigator(ss);
		
		query = getQuery();
		JSON = new JSON(ss);
		
		db = ss.service.getKVSService();
		memcached = ss.service.getMemcachedService();
		repository = ss.service.getRepositoryService();
	}
	
	public Object getQuery(){
		Scriptable o = newScriptable();
		Map<String, String[]> map = ss.request.getParameterMap();
		for (Iterator<Entry<String, String[]>> i = map.entrySet().iterator(); i
				.hasNext();) {
			Entry<String, String[]> e = i.next();
			if(e.getValue().length == 1){
				o.put(e.getKey(), o, e.getValue()[0]);
			}else if(e.getValue().length > 1){
				Scriptable l = newScriptable();
				for (int j = 0; j < e.getValue().length; j++) {
					l.put(j, l, e.getValue()[j]);
				}
				o.put(e.getKey(), o, l);
			}
		}
		if(!o.has("path", o))
			o.put("path", o, ss.pathArg);
		return o;
	}
	
	public void require(String path) throws IOException, URISyntaxException{
		JSOM om = ss.vm.compile(resolvePath(path));
		if(!ss.required.contains(om)){
			ss.required.add(om);
		}
		om.run(ss.scope);
	}
	
	public void require_once(String path) throws IOException, URISyntaxException{
		JSOM om = ss.vm.compile(resolvePath(path));
		if(!ss.required.contains(om)){
			ss.required.add(om);
			om.run(ss.scope);
		}
	}

	public HTML5Template load_html(String uri) throws IOException, URISyntaxException{
		URI u = ss.uri.resolve(uri);
		if(ss.resolver.exist(u.toString())){
			HTML5OM om = ss.deck.compile(u);
			if(om != null){
				return new HTML5Template(om, u);
			}
		}
		return null;
	}
	
	public String load_file(String path) throws IOException, URISyntaxException{
		if(ss.resolver.exist(resolvePath(path).toString())){
			return QrONEUtils.convertStreamToString(ss.resolver.getInputStream(resolvePath(path)));
		}
		return null;
	}
	

	public void redirect(String uri) throws URISyntaxException{
		String url = ss.uri.resolve(uri).toString();
		if(!url.startsWith("http://") && !url.startsWith("https://")){
			url = "http://" + ss.request.getRemoteHost() + url;
		}
		
		header("Location: " + url);
	}
	
	
	
	private PropertiesDeck propDeck;
	public Object load_properties(String path) throws IOException, URISyntaxException{
		if(propDeck == null)
			propDeck = new PropertiesDeck(ss.resolver);
		return propDeck.compile(ss.uri.resolve(path));
	}

	private YamlDeck yamlDeck;
	public Object load_yaml(String path) throws IOException, URISyntaxException{
		if(yamlDeck == null)
			yamlDeck = new YamlDeck(ss.resolver);
		return yamlDeck.compile(ss.uri.resolve(path));
	}
	
	private TextileDeck textileDeck;
	public String load_textile(String path) throws IOException, URISyntaxException{
		if(textileDeck == null)
			textileDeck = new TextileDeck(ss.resolver);
		return textileDeck.compile(ss.uri.resolve(path));
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

	public void header(String header, String value){
		ss.response.addHeader(header, value);
	}
	
	public void header(String header){
		String[] hs = header.split(":", 2);
		ss.response.addHeader(hs[0], hs[1]);
	}

	public String stringify(Object out){
		return net.arnx.jsonic.JSON.encode(out);
	}
	
	public String loginURL(String url, Scriptable attributes, String doneURL){
		Map<String, String> attrMap = new HashMap<String, String>();
		if(attributes != null){
			Object[] ids = attributes.getIds();
			for (int i = 0; i < ids.length; i++) {
				if(ids[i] instanceof String){
					Object v = attributes.get((String)ids[i], attributes);
					if(v instanceof String){
						attrMap.put((String)ids[i], (String)v);
					}
				}
			}
		}
		return ss.service.getLoginService().loginURL(url, attrMap, doneURL);
	}

	public String loginURL(String doneURL) {
		return ss.service.getLoginService().loginURL(doneURL);
	}
	
	public String logoutURL(String doneURL){
		return ss.service.getLoginService().logoutURL(doneURL);
	}
	
	public User getUser(){
		return ss.service.getLoginService().getUser();
	}
}

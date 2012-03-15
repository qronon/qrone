package org.qrone.r7.script.browser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.URLCodec;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.qrone.database.DatabaseService;
import org.qrone.login.SecurityService;
import org.qrone.memcached.MemcachedService;
import org.qrone.r7.PortingService;
import org.qrone.r7.RepositoryService;
import org.qrone.r7.fetcher.HTTPFetcher;
import org.qrone.r7.format.JSON;
import org.qrone.r7.format.JavaProperties;
import org.qrone.r7.format.Textile;
import org.qrone.r7.format.YAML;
import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.HTML5OM;
import org.qrone.r7.parser.HTML5Template;
import org.qrone.r7.parser.JSDeck;
import org.qrone.r7.parser.JSOM;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.script.ServletScope;
import org.qrone.util.QrONEUtils;
import org.qrone.util.QueryString;


public class Window{
	private PortingService service;
	private Scriptable scope;
	private HTML5Deck deck;
	private JSDeck vm;
	private Set<JSOM> required = new ConcurrentSkipListSet<JSOM>();
	private URIResolver resolver;
	
	private ServletScope ss;
	public HttpServletRequest request;
	public HttpServletResponse response;
	
	public Document document;
	public Location location;
	public Navigator navigator;
	public Scriptable query;

	public DatabaseService db;
	public MemcachedService memcached;
	public RepositoryService repository;
	public SecurityService security;
	public HTTPFetcher http;

	public JSON JSON;
	public YAML YAML;
	public Textile Textile;
	public JavaProperties JavaProperties;
	
	public Window(ServletScope ss, Scriptable scope, 
			HTML5Deck deck, JSDeck vm, PortingService service) throws IOException, URISyntaxException{
		this.ss = ss;
		this.request = ss.request;
		this.response = ss.response;
		this.scope = scope;
		this.deck = deck;
		this.vm = vm;
		
		this.service = service;
		db = service.getDatabaseService();
		memcached = service.getMemcachedService();
		repository = service.getRepositoryService();
		security = service.getSecurityService();
		resolver = service.getURIResolver();
		http = service.getURLFetcher();

		document = new Document(request, response, deck, 
				ss.uri.toString().replaceAll("\\.server\\.js$", ".html"),
				security.getTicket(request));
		location = new Location(request);
		navigator = new Navigator(request);
		
		JSON = new JSON(resolver, scope);
		YAML = new YAML(resolver);
		Textile = new Textile(resolver);
		JavaProperties = new JavaProperties(resolver);
	}
	
	public void init(Scriptable scope){
		Scriptable req = newScriptable();
		scope.put("request", scope, req);
		
		req.setPrototype((Scriptable)Context.javaToJS(request,req));

		req.put("url", req, request.getRequestURL().toString());
		req.put("uri", req, ss.uri.toString());
		req.put("path", req, ss.path.toString());
		if(ss.leftpath.length() > 0)
			req.put("leftpath", req, ss.leftpath.toString());
		
		query = toScriptable(ss.get);
		req.put("get", req, query);
		
		req.put("body", req, ss.body);
		req.put("text", req, ss.text);
		
		if(security == null || security.validateTicket(request,ss.getParameter(".ticket"))){
			Scriptable post = toScriptable(ss.post);
			req.put("post", req, post);
			req.put("secure", req, true);
		}else{
			req.put("secure", req, false);
		}
	}

	private Scriptable newScriptable(){
		return JSDeck.getContext().newObject(scope);
	}

	public Scriptable toScriptable(Map<String, List<String>> map){
		Scriptable o = newScriptable();
		for (Iterator<Entry<String, List<String>>> i = map.entrySet().iterator(); i
				.hasNext();) {
			Entry<String, List<String>> e = i.next();
			if(e.getValue().size() == 1){
				o.put(e.getKey(), o, e.getValue().get(0));
			}else if(e.getValue().size() > 1){
				Scriptable l = JSDeck.getContext().newArray(scope, e.getValue().size());
				for (int j = 0; j < e.getValue().size(); j++) {
					l.put(j, l, e.getValue().get(j));
				}
				o.put(e.getKey(), o, l);
			}
		}
		return o;
	}
	
	private URI resolvePath(String path) throws URISyntaxException {
		return ss.uri.resolve(new URI(path));
	}
	
	
	public void require(String path) throws IOException, URISyntaxException{
		JSOM om = vm.compile(resolvePath(path));
		if(!required.contains(om)){
			required.add(om);
		}
		om.run(scope);
	}
	
	public void require_once(String path) throws IOException, URISyntaxException{
		JSOM om = vm.compile(resolvePath(path));
		if(!required.contains(om)){
			required.add(om);
			om.run(scope);
		}
	}

	public HTML5Template load_template(String uri) throws IOException, URISyntaxException{
		URI u = resolvePath(uri);
		if(resolver.exist(u.toString())){
			HTML5OM om = deck.compile(u);
			if(om != null){
				return new HTML5Template(om, u, security.getTicket(request));
			}
		}
		return null;
	}
	
	public String load_file(String path) throws IOException, URISyntaxException{
		if(resolver.exist(resolvePath(path).toString())){
			return QrONEUtils.convertStreamToString(resolver.getInputStream(resolvePath(path)));
		}
		return null;
	}
	

	public void redirect(String uri) throws URISyntaxException{
		String url = resolvePath(uri).toString();
		if(!url.startsWith("http://") && !url.startsWith("https://")){
			url = "http://" + request.getRemoteHost() + url;
		}
		
		header("Location: " + url);
	}
	
	/*
	private JavaProperties propDeck;
	public Object load_properties(String path) throws IOException, URISyntaxException{
		if(propDeck == null)
			propDeck = new JavaProperties(resolver);
		return propDeck.compile(resolvePath(path));
	}

	private YAML yamlDeck;
	public Object load_yaml(String path) throws IOException, URISyntaxException{
		if(yamlDeck == null)
			yamlDeck = new YAML(resolver);
		return yamlDeck.compile(resolvePath(path));
	}
	
	private Textile textileDeck;
	public String load_textile(String path) throws IOException, URISyntaxException{
		if(textileDeck == null)
			textileDeck = new Textile(resolver);
		return textileDeck.compile(resolvePath(path));
	}
	*/
	
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
	
	/*
	public String serialize(Scriptable obj) throws IOException{
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ScriptableOutputStream out = new ScriptableOutputStream(bo, scope.getPrototype());
		out.writeObject(obj);
		out.close();
		return QrONEUtils.base64_encode(bo.toByteArray());
	}
	
	public Object deserialize(String ser) throws IOException, ClassNotFoundException{
		ByteArrayInputStream bin = new ByteArrayInputStream(QrONEUtils.base64_decode(ser));
		ObjectInputStream in = new ScriptableInputStream(bin, scope.getPrototype());
		Object deserialized = in.readObject();
		in.close();
		return deserialized;
	}
	*/
	
	private String digest_safe(String type, String data){
		try {
			return digest(type, data);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String digest(String type, String data) throws NoSuchAlgorithmException{
		return QrONEUtils.digest(type, data);
	}

	public void header(String header, String value){
		response.addHeader(header, value);
	}
	
	public void header(String header){
		String[] hs = header.split(":", 2);
		response.addHeader(hs[0], hs[1]);
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
		return service.getLoginService().loginURL(url, attrMap, doneURL);
	}

	public String loginURL(String doneURL) {
		return service.getLoginService().loginURL(doneURL);
	}
	
	public String logoutURL(String doneURL){
		return service.getLoginService().logoutURL(doneURL);
	}
	
	public User getUser(){
		return service.getLoginService().getUser();
	}
}

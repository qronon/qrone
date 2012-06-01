package org.qrone.r7.script.browser;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.qrone.r7.resolver.URIFileSystem;
import org.qrone.r7.script.ServletScope;
import org.qrone.util.Digest;
import org.qrone.util.QrONEUtils;
import org.qrone.util.QueryString;
import org.qrone.util.Stream;


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
	public Map<String, Object> query;
	public User user;

	public DatabaseService db;
	public MemcachedService memcached;
	public RepositoryService repository;
	public HTTPFetcher http;
	
	public URIFileSystem fs;

	public JSON JSON;

	public String home = new File(".").getAbsoluteFile().getParentFile().getAbsolutePath();
	
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
		resolver = service.getURIResolver();
		http = service.getURLFetcher();

		user = (User)request.getAttribute("User");
		
		document = new Document(request, response, deck, 
				ss.uri.toString().replaceAll("\\.server\\.js$", ".html"),
				user.getTicket());
		location = new Location(request);
		navigator = new Navigator(request);
		
		fs = service.getFileSystemService();

		JSON = new JSON(resolver, scope, vm.getContext());
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
		
		query = ss.get;
		req.put("get", req, query);
		req.put("post", req, ss.post);
		req.put("body", req, ss.body);
		req.put("text", req, ss.text);
	}
	
	public PortingService getPortingService(){
		return service;
	}

	private Scriptable newScriptable(){
		return vm.getContext().newObject(scope);
	}

	/*
	public Scriptable toScriptable(Map<String, List<String>> map){
		Scriptable o = newScriptable();
		for (Iterator<Entry<String, List<String>>> i = map.entrySet().iterator(); i
				.hasNext();) {
			Entry<String, List<String>> e = i.next();
			if(e.getValue().size() == 1){
				o.put(e.getKey(), o, e.getValue().get(0));
			}else if(e.getValue().size() > 1){
				Scriptable l = vm.getContext().newArray(scope, e.getValue().size());
				for (int j = 0; j < e.getValue().size(); j++) {
					l.put(j, l, e.getValue().get(j));
				}
				o.put(e.getKey(), o, l);
			}
		}
		return o;
	}
	*/
	
	public URI resolvePath(String path) throws URISyntaxException {
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
				return new HTML5Template(om, u, user.getTicket());
			}
		}
		return null;
	}
	
	public String load_file(String path) throws IOException, URISyntaxException{
		if(resolver.exist(resolvePath(path).toString())){
			return new String(Stream.read(resolver.getInputStream(resolvePath(path))),"utf8");
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
	
	/*
	public String openid_login_url(String url, Map attributes, String doneURL){
		Map<String, String> attrMap = new HashMap<String, String>();
		return service.getLoginService().getOpenIDLoginURL(url, attributes, doneURL);
	}

	public String google_openid_login_url(String doneURL) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("login", "http://axschema.org/contact/email");
		return openid_login_url("https://www.google.com/accounts/o8/id", map, doneURL);
	}
	
	public String logout_url(String doneURL){
		return service.getLoginService().getLogoutURL(doneURL);
	}
	*/
	
	
	public void login(String id){
		user.login(id);
	}
	
	public void logout(){
		user.logout();
	}

	public void close() throws IOException {

		document.flush();
		document.close();
		ss.close();
		user.close();
	}
}

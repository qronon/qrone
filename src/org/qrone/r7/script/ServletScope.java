package org.qrone.r7.script;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.util.QrONEUtils;
import org.qrone.util.QueryString;
import org.qrone.util.Stream;

public class ServletScope{
	public HttpServletRequest request;
	public HttpServletResponse response;
	public URI uri;
	public String path;
	public String leftpath;

	public byte[] body;
	public String text;
	public Map<String, List<String>> get;
	public Map<String, List<String>> post;
	
	public ServletScope(HttpServletRequest request, HttpServletResponse response, URI uri, String path, String leftpath) {
		this.request = request;
		this.response = response;
		this.uri = uri;
		this.path = path;
		this.leftpath = leftpath;
		
		get = parseQueryString(request.getQueryString());
		
		try {
			InputStream in = request.getInputStream();
			body = Stream.read(in);
			text = QrONEUtils.getString(body, request.getHeader("Content-Type"));
			post = parseQueryString(text);
		} catch (IOException e) {}
	}
	
	public String getParameter(String name){
		List<String> list = get.get(name);
		if(list != null && list.size() > 0){
			return list.get(0);
		}

		list = post.get(name);
		if(list != null && list.size() > 0){
			return list.get(0);
		}
		
		return null;
	}

	private Map<String, List<String>>  parseQueryString(String query){
		QueryString qs = new QueryString(query);
		return qs.getParameterMap();
	}
}
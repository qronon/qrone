package org.qrone.r7.script.browser;

import javax.servlet.http.HttpServletRequest;

public class Location{
	
	public Location(HttpServletRequest request){
		href = request.getRequestURL().toString();
		protocol = request.getProtocol();
		hostname = request.getServerName();
		host = request.getServerName() + ":" + request.getServerPort();
		port = request.getServerPort();
		pathname = request.getPathInfo();
		search = request.getQueryString();
		hash = "";
	}
	
	public String href;
	public String protocol;
	public String host;
	public String hostname;
	public int port;
	public String pathname;
	public String search;
	public String hash;
}

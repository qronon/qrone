package org.qrone.r7.script.browser;

import java.io.IOException;

import org.qrone.r7.script.ServletScope;
import org.qrone.r7.script.ServletScopeObject;

public class Location extends ServletScopeObject{
	
	public Location(ServletScope ss) throws IOException{
		super(ss);
		
		href = ss.request.getRequestURL().toString();
		protocol = ss.request.getProtocol();
		hostname = ss.request.getServerName();
		host = ss.request.getServerName() + ":" + ss.request.getServerPort();
		port = ss.request.getServerPort();
		pathname = ss.request.getPathInfo();
		search = ss.request.getQueryString();
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

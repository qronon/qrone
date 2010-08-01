package org.qrone.r7.script;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.qrone.r7.ObjectConverter;
import org.qrone.r7.parser.HTML5Element;
import org.qrone.r7.parser.HTML5OM;
import org.qrone.r7.parser.HTML5Template;
import org.qrone.r7.parser.NodeLister;

public class Location extends JSObject{
	
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

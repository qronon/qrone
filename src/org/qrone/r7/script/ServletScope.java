package org.qrone.r7.script;

import java.net.URI;

public class ServletScope{
	public URI uri;
	public String path;
	public String leftpath;
	
	public ServletScope(URI uri, String path, String leftpath) {
		this.uri = uri;
		this.path = path;
		this.leftpath = leftpath;
	}
	
}
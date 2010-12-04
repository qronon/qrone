package org.qrone.r7.script;

import java.net.URI;

public class ServletScope {
	public URI uri;
	public String path;
	public String pathArg;
	
	public ServletScope(URI uri, String path, String pathArg) {
		this.uri = uri;
		this.path = path;
		this.pathArg = pathArg;
	}
}
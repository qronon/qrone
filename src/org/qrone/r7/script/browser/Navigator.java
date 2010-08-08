package org.qrone.r7.script.browser;

import java.io.IOException;

import org.qrone.r7.script.ServletScope;
import org.qrone.r7.script.ServletScopeObject;

public class Navigator extends ServletScopeObject{
	
	public Navigator(ServletScope ss) throws IOException{
		super(ss);
		userAgent = ss.request.getHeader("UserAgent");
	}
	
	public String userAgent;
}

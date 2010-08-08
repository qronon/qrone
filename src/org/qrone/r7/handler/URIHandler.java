package org.qrone.r7.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface URIHandler { 
	public boolean handle(HttpServletRequest request, HttpServletResponse response);
}

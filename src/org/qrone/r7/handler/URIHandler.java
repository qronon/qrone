package org.qrone.r7.handler;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface URIHandler {
	public boolean handle(HttpServletRequest request, HttpServletResponse response,
			String uri, String requestPath, String requestPathArg);
}

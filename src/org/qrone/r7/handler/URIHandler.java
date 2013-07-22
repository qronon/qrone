package org.qrone.r7.handler;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface URIHandler {
	public boolean handle(HttpServletRequest request, HttpServletResponse response,
			String uri, String path, String leftpath, List<String> arg);
}

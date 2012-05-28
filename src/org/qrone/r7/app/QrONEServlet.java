package org.qrone.r7.app;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class QrONEServlet extends HttpServlet {
	
	private String path;
	private Map<String, QrONEURIHandler> map = new Hashtable<String, QrONEURIHandler>();
	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String host = request.getHeader("Host");
		QrONEURIHandler h = map.get(host);
		if(h == null){
			h = new QrONEURIHandler(getServletContext(), host, path);
			map.put(host, h);
		}
		h.handle(request, response, request.getPathInfo(), "", "");
	}
	
	public void clean(){
		for (QrONEURIHandler h : map.values()) {			
			h.clean();
		}
	}

	public void setLocalFilePath( String path) {
		this.path = path;
	}
}

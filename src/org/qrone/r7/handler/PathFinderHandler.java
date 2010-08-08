package org.qrone.r7.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PathFinderHandler implements URIHandler {
	private URIHandler handler;
	
	public PathFinderHandler(URIHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public boolean handle(HttpServletRequest request,
			HttpServletResponse response, String path) {
		int index = path.lastIndexOf('/');
		if(index > 0){
			handle(request, response, path.substring(0, index));
		}
		
		if(path.endsWith("/")){
			if(handler.handle(request, response, path + "index")) return true;
		}else{
			if(handler.handle(request, response, path)) return true;
		}
		
		return false;
	}

}

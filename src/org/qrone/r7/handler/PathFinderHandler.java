package org.qrone.r7.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PathFinderHandler implements URIHandler {
	private URIHandler handler;
	
	public PathFinderHandler(URIHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public boolean handle(HttpServletRequest request, HttpServletResponse response, 
			String uri, String path, String leftpath) {
		
		if(path.endsWith("/")){
			if(handler.handle(request, response, uri + "index", path + "index", leftpath)) 
				return true;
		}else{
			if(handler.handle(request, response, uri, path, leftpath)) 
				return true;
		}
		
		int index = path.lastIndexOf('/');
		if(index > 0){
			if(handle(request, response, uri.substring(0, index), path.substring(0, index), 
					path.substring(index) + leftpath))
				return true;
		}
		
		return false;
	}

}

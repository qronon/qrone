package org.qrone.r7.handler;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PathFinderHandler implements URIHandler {
	private URIHandler handler;
	
	public PathFinderHandler(URIHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public boolean handle(HttpServletRequest request, HttpServletResponse response, 
			String uri, String path, String pathArg) {
		
		if(path.endsWith("/")){
			if(handler.handle(request, response, uri + "index", path + "index", pathArg)) 
				return true;
		}else{
			if(handler.handle(request, response, uri, path, pathArg)) 
				return true;
		}
		
		int index = path.lastIndexOf('/');
		if(index > 0){
			if(handle(request, response, uri.substring(0, index), path.substring(0, index), 
					path.substring(index) + pathArg))
				return true;
		}
		
		return false;
	}

}

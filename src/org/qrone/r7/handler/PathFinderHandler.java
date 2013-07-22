package org.qrone.r7.handler;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.r7.resolver.URIResolver;

public class PathFinderHandler implements URIHandler {
	private URIHandler handler;
	private URIResolver resolver;
	
	public PathFinderHandler(URIHandler handler, URIResolver resolver) {
		this.handler = handler;
		this.resolver = resolver;
	}
	
	@Override
	public boolean handle(HttpServletRequest request, HttpServletResponse response, 
			String uri, String path, String leftpath, List<String> arg) {
		
		if(path.endsWith("/")){
			if(handler.handle(request, response, uri + "index", path + "index", leftpath, arg)) 
				return true;
		}else{
			if(handler.handle(request, response, uri, path, leftpath, arg)) 
				return true;
		}
		
		return rhandle(request, response, "", path, uri, new ArrayList<String>());
	}
	
	public boolean rhandle(HttpServletRequest request, HttpServletResponse response, 
			String uri, String path, String leftpath, List<String> arg) {
		// uri=/qrone/test1/test2 path=/qrone leftpath=/test1/test2/my
		int index = leftpath.indexOf('/',1);
		if(index > 0){
			
			if(resolver.existPath(uri + "/_any")){
				arg.add(leftpath.substring(1,index));
				
				if(handler.handle(request, response, uri + "/_any", path, leftpath, arg))
					return true;

				// uri=/qrone/test1/test2 path=/qrone/_any leftpath=/test2
				return rhandle(request, response, uri + "/_any", path, leftpath.substring(index), arg);
				
			}else{
				String left = uri + leftpath.substring(0,index);
				if(handler.handle(request, response, left, path, leftpath, arg))
					return true;
				
				// uri=/qrone/test1/test2 path=/qrone/test1 leftpath=/test2
				return rhandle(request, response, left, path, leftpath.substring(index), arg);
			}
			
		}else{

			if(handler.handle(request, response, uri + leftpath, path, "", arg))
				return true;
		}
		
		return false;
	}

}

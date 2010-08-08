package org.qrone.r7.appengine;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.r7.handler.CascadeHandler;
import org.qrone.r7.handler.HTML5Handler;
import org.qrone.r7.handler.ResolverHandler;
import org.qrone.r7.handler.URIHandler;
import org.qrone.r7.resolver.CascadeResolver;
import org.qrone.r7.resolver.FilteredResolver;
import org.qrone.r7.resolver.InternalResourceResolver;
import org.qrone.r7.resolver.MemoryResolver;
import org.qrone.r7.resolver.ServletResolver;
 
public class AppEngineURIHandler implements URIHandler {
	protected CascadeResolver resolver;
	protected CascadeHandler handler;
	
	public AppEngineURIHandler(ServletContext cx) {
		resolver = new CascadeResolver();
		resolver.add(new FilteredResolver("/qrone-server/", new InternalResourceResolver()));
		resolver.add(new MemoryResolver());
		resolver.add(new ServletResolver(cx));
		
		handler = new CascadeHandler();
		handler.add(new HTML5Handler(resolver, new AppEngineImageBufferService()));
		handler.add(new ResolverHandler(resolver));
	}

	@Override
	public boolean handle(HttpServletRequest request,
			HttpServletResponse response) {
		return handler.handle(request, response);
	}
}

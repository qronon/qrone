package org.qrone.r7.handler;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.r7.app.AwtImageBufferService;
import org.qrone.r7.resolver.CascadeResolver;
import org.qrone.r7.resolver.FileResolver;
import org.qrone.r7.resolver.MemoryResolver;

public class LocalURIHandler implements URIHandler {
	protected CascadeResolver resolver;
	protected CascadeHandler handler;
	
	public LocalURIHandler(ServletContext cx) {
		resolver = new CascadeResolver();
		resolver.add(new MemoryResolver());
		resolver.add(new FileResolver(new File(".")));
		
		handler = new CascadeHandler();
		handler.add(new HTML5Handler(resolver, new AwtImageBufferService()));
		handler.add(new ResolverHandler(resolver));
	}

	@Override
	public boolean handle(HttpServletRequest request,
			HttpServletResponse response) {
		return handler.handle(request, response);
	}
}

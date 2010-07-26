package org.qrone.r7.app;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.img.AwtImageBufferService;
import org.qrone.r7.handler.CascadeHandler;
import org.qrone.r7.handler.HTML5Handler;
import org.qrone.r7.handler.ResolverHandler;
import org.qrone.r7.handler.URIHandler;
import org.qrone.r7.resolver.CascadeResolver;
import org.qrone.r7.resolver.FileResolver;
import org.qrone.r7.resolver.FilteredResolver;
import org.qrone.r7.resolver.InternalResourceResolver;
import org.qrone.r7.resolver.MemoryResolver;

public class LocalURIHandler implements URIHandler {
	protected CascadeResolver resolver;
	protected CascadeHandler handler;
	
	public LocalURIHandler() {
		resolver = new CascadeResolver();
		resolver.add(new FilteredResolver("/qrone-server/", new InternalResourceResolver()));
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
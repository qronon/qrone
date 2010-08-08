package org.qrone.r7.appengine;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.r7.Extendable;
import org.qrone.r7.ExtensionIndex;
import org.qrone.r7.app.AwtImageBufferService;
import org.qrone.r7.handler.CascadeHandler;
import org.qrone.r7.handler.ExtendableURIHandler;
import org.qrone.r7.handler.HTML5Handler;
import org.qrone.r7.handler.ResolverHandler;
import org.qrone.r7.handler.URIHandler;
import org.qrone.r7.resolver.CascadeResolver;
import org.qrone.r7.resolver.FilteredResolver;
import org.qrone.r7.resolver.InternalResourceResolver;
import org.qrone.r7.resolver.MemoryResolver;
import org.qrone.r7.resolver.ServletResolver;
 
public class AppEngineURIHandler extends ExtendableURIHandler{
	public AppEngineURIHandler(ServletContext cx) {
		resolver.add(new FilteredResolver("/qrone-server/", new InternalResourceResolver()));
		resolver.add(new MemoryResolver());
		resolver.add(new ServletResolver(cx));
		
		HTML5Handler html5handler = new HTML5Handler(
				resolver, new AppEngineImageBufferService());
		ExtensionIndex ei = new ExtensionIndex();
		if(ei.unpack(resolver) == null){
			ei.find(cx);
			ei.pack(resolver);
		}
		ei.extend(html5handler);
		ei.extend(this);

		handler.add(html5handler);
		handler.add(new ResolverHandler(resolver));
	}
	
}

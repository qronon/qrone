package org.qrone.r7.handler;

import java.io.File;

import javax.servlet.ServletContext;

import org.qrone.r7.ExtensionIndex;
import org.qrone.r7.app.AwtImageBufferService;
import org.qrone.r7.resolver.FileResolver;
import org.qrone.r7.resolver.MemoryResolver;
import org.qrone.r7.store.MemoryStore;

public class LocalURIHandler extends ExtendableURIHandler{
	
	public LocalURIHandler(ServletContext cx) {
		resolver.add(new MemoryResolver());
		resolver.add(new FileResolver(new File(".")));
		
		HTML5Handler html5handler = new HTML5Handler(
				resolver, new MemoryStore(), new AwtImageBufferService());
		ExtensionIndex ei = new ExtensionIndex();
		if(ei.unpack(resolver) == null){
			ei.find();
			ei.pack(resolver);
		}
		ei.extend(html5handler);
		ei.extend(this);
		
		handler.add(html5handler);
		handler.add(new ResolverHandler(resolver));
	}
}

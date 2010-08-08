package org.qrone.r7.handler;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.r7.Extendable;
import org.qrone.r7.ExtensionIndex;
import org.qrone.r7.app.AwtImageBufferService;
import org.qrone.r7.resolver.CascadeResolver;
import org.qrone.r7.resolver.FileResolver;
import org.qrone.r7.resolver.MemoryResolver;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.script.ScriptableJavaObject;
import org.qrone.r7.tag.HTML5TagHandler;

public class LocalURIHandler extends ExtendableURIHandler{
	
	public LocalURIHandler(ServletContext cx) {
		resolver.add(new MemoryResolver());
		resolver.add(new FileResolver(new File(".")));
		
		HTML5Handler html5handler = new HTML5Handler(
				resolver, new AwtImageBufferService());
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

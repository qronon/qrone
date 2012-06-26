package org.qrone.r7.handler;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.r7.Extendable;
import org.qrone.r7.resolver.CascadeResolver;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.script.ext.ClassPrototype;
import org.qrone.r7.script.ext.ListPrototype;
import org.qrone.r7.script.ext.MapPrototype;
import org.qrone.r7.script.window.WindowEncodes;
import org.qrone.r7.script.window.WindowFileSystem;
import org.qrone.r7.script.window.WindowFormats;

public abstract class ExtendableURIHandler implements URIHandler, Extendable{
	protected CascadeResolver resolver;
	protected CascadeHandler handler;
	
	public ExtendableURIHandler() {
		resolver = new CascadeResolver();
		handler = new CascadeHandler();
	}

	@Override
	public boolean handle(HttpServletRequest request, HttpServletResponse response, 
			String uri, String path, String leftpath) {
		return handler.handle(request, response, uri, path, leftpath);
	}
	
	public void addExtension(Class c){
		try {
			if(URIHandler.class.isAssignableFrom(c)){
				handler.add((URIHandler)c.getConstructor(URIResolver.class).newInstance(resolver));
			}else if(URIResolver.class.isAssignableFrom(c)){
				resolver.add((URIResolver)c.getConstructor().newInstance());
			}
		} catch (IllegalArgumentException e) {
		} catch (SecurityException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		} catch (NoSuchMethodException e) {
		}
	}

	protected void rawextend(Extendable e){
		e.addExtension(ClassPrototype.class);
		e.addExtension(MapPrototype.class);
		e.addExtension(ListPrototype.class);
		
		e.addExtension(WindowEncodes.class);
		e.addExtension(WindowFormats.class);
		e.addExtension(WindowFileSystem.class);
	}
}

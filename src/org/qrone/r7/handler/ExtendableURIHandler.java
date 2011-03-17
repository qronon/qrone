package org.qrone.r7.handler;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.r7.Extendable;
import org.qrone.r7.resolver.CascadeResolver;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.script.ext.ClassPrototype;
import org.qrone.r7.script.ext.ScriptableList;
import org.qrone.r7.script.ext.ScriptableMap;
import org.qrone.r7.tag.ImageHandler;
import org.qrone.r7.tag.Scale9Handler;
import org.qrone.r7.tag.SecurityTicketHandler;

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
		e.addExtension(ScriptableMap.class);
		e.addExtension(ScriptableList.class);
		e.addExtension(ClassPrototype.class);
		e.addExtension(ImageHandler.class);
		e.addExtension(Scale9Handler.class);
		e.addExtension(SecurityTicketHandler.class);
	}
}

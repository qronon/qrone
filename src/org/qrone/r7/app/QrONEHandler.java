package org.qrone.r7.app;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.r7.handler.CascadeHandler;
import org.qrone.r7.handler.HTML5Handler;
import org.qrone.r7.handler.ResolverHandler;
import org.qrone.r7.handler.URIHandler;
import org.qrone.r7.resolver.CascadeResolver;
import org.qrone.r7.resolver.FileResolver;
import org.qrone.r7.resolver.FilteredResolver;
import org.qrone.r7.resolver.InternalResourceResolver;
import org.qrone.r7.resolver.MemoryResolver;

public class QrONEHandler extends StandaloneURIHandler {
	public QrONEHandler() {
		resolver = new CascadeResolver();
		resolver.add(0,new FilteredResolver("/qrone-server/", new InternalResourceResolver()));
	}
}

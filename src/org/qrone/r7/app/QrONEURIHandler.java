package org.qrone.r7.app;

import javax.servlet.ServletContext;

import org.qrone.r7.handler.LocalURIHandler;
import org.qrone.r7.resolver.FilteredResolver;
import org.qrone.r7.resolver.InternalResourceResolver;

public class QrONEURIHandler extends LocalURIHandler {
	public QrONEURIHandler(ServletContext cx) {
		super(cx);
		resolver.add(0,new FilteredResolver("/qrone-server/", new InternalResourceResolver()));
	}
}


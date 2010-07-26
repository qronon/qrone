package org.qrone.r7.app;

import org.qrone.r7.handler.LocalURIHandler;
import org.qrone.r7.resolver.CascadeResolver;
import org.qrone.r7.resolver.FilteredResolver;
import org.qrone.r7.resolver.InternalResourceResolver;

public class QrONEHandler extends LocalURIHandler {
	public QrONEHandler() {
		resolver = new CascadeResolver();
		resolver.add(0,new FilteredResolver("/qrone-server/", new InternalResourceResolver()));
	}
}


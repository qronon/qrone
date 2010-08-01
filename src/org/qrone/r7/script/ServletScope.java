package org.qrone.r7.script;

import java.io.PrintWriter;
import java.net.URI;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.JSDeck;
import org.qrone.r7.parser.JSOM;
import org.qrone.r7.resolver.URIResolver;

public class ServletScope {
	public Scriptable scope;
	public HttpServletRequest request;
	public HttpServletResponse response;
	public JSDeck vm;
	public HTML5Deck deck;
	public URIResolver resolver;
	public String path;
	public Set<JSOM> required = new ConcurrentSkipListSet<JSOM>();
	public URI uri;
	public PrintWriter writer;
}
package org.qrone.r7.handler;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.HttpURI;
import org.mortbay.proxy.AsyncProxyServlet;
import org.qrone.r7.script.ServletScope;

public class ProxyHandler implements URIHandler{
	private ServletConfig config;
	private String host;
	private int port;

	public ProxyHandler(ServletConfig config) {
		this(config, null,-1);
	}
	
	public ProxyHandler(ServletConfig config, int port) {
		this(config, null, port);
	}

	public ProxyHandler(ServletConfig config, String host) {
		this(config, host, -1);
	}
	
	public ProxyHandler(ServletConfig config, String host, int port) {
		this.config = config;
		this.host = host;
		this.port = port;
	}
	@Override
	public boolean handle(final HttpServletRequest request,
			final HttpServletResponse response, final String uri, final String path, final String pathArg) {
		try {
			AsyncProxyServlet serv = new AsyncProxyServlet(){
				@Override
				protected HttpURI proxyHttpURI(String scheme,
						String serverName, int serverPort, String uri)
						throws MalformedURLException {
					String query = request.getQueryString();
					return new HttpURI("http://" + 
							(host != null ? host : serverName) + 
							(port > 0 && port != 80 ? ":" + port : "") + 
							"/" + path + pathArg + 
							(query != null ? "?" + query : ""));
				}
			};
			serv.init(config);
			serv.service(request, response);
			return true;
		} catch (ServletException e) {
		} catch (IOException e) {
		}
		return false;
	}
	
	public static void proxy(final HttpServletRequest request, final HttpServletResponse response, 
			final String path, final String pathArg, final String host, final int port, final String altPath) 
				throws ServletException, IOException {
		new AsyncProxyServlet(){
			@Override
			protected HttpURI proxyHttpURI(String scheme,
					String serverName, int serverPort, String uri)
					throws MalformedURLException {
				String query = request.getQueryString();
				return new HttpURI("http://" + 
						(host != null ? host : serverName) + 
						(port > 0 && port != 80 ? ":" + port : "") + 
						"/" + 
						(altPath != null ? altPath : path + pathArg) + 
						(query != null ? "?" + query : ""));
			}
		}.service(request, response);
	}

}

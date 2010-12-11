package org.qrone.r7.fetcher;

import java.net.URL;
import java.util.Map;

public class HTTPRequest {
	private URL url;
	private boolean followRedirect;
	private String method;
	private byte[] payload;
	private Map<String,String> headers;
	
	public HTTPRequest(
			URL url,
			String method,byte[] payload,
			Map<String,String> headers,boolean followRedirect) {
		this.url = url;
		this.followRedirect = followRedirect;
		this.payload = payload;
		this.headers = headers;
	}
	
	public URL getURL() {
		return url;
	}
	public void setURL(URL url) {
		this.url = url;
	}
	public boolean isFollowRedirect() {
		return followRedirect;
	}
	public void setFollowRedirect(boolean followRedirect) {
		this.followRedirect = followRedirect;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public byte[] getPayload() {
		return payload;
	}
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	public Map<String, String> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

}

package org.qrone.r7.fetcher;

import java.io.InputStream;
import java.util.Map;

public class HTTPResponse {
	private InputStream in;
	private Map<String,String> headers;
	private int responseCode;
	
	public HTTPResponse(InputStream in,
			Map<String,String> headers, int responseCode) {
		this.in = in;
		this.headers = headers;
		this.responseCode = responseCode;
	}
	
	public InputStream getInputStream() {
		return in;
	}
	public Map<String,String> getHeaders() {
		return headers;
	}
	public int getResponseCode() {
		return responseCode;
	}
}
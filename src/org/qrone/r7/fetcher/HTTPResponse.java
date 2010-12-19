package org.qrone.r7.fetcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.qrone.util.QrONEUtils;

public class HTTPResponse {
	private InputStream in;
	private Map<String,String> headers;
	private int responseCode;
	private byte[] body;
	private String text;
	
	public HTTPResponse(InputStream in,
			Map<String,String> headers, int responseCode) {
		this.in = in;
		this.headers = headers;
		this.responseCode = responseCode;
	}
	
	public InputStream getInputStream() {
		return in;
	}
	
	public byte[] getBody() throws IOException{
		if(body == null){
			body = QrONEUtils.read(in);
		}
		return body;
	}

	public String getContent() throws IOException{
		if(text == null){
			text = QrONEUtils.getString(getBody(), headers.get("Content-Type"));
		}
		return text;
	}
	
	public Map<String,String> getHeaders() {
		return headers;
	}
	public int getResponseCode() {
		return responseCode;
	}
}
package org.qrone.r7.fetcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;

import org.qrone.util.QrONEUtils;

import com.ibm.icu.text.CharsetDetector;

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
	
	public byte[] getBody() throws IOException{
		return QrONEUtils.read(in);
	}

	public String getContent() throws IOException{
		String contentType = headers.get("Content-Type");
		String encoding = null;
		if(contentType != null){
			int idx = contentType.indexOf("charset=");
			if(idx >= 0){
				try{
					encoding = contentType.substring(idx);
					CharsetDetector cd = new CharsetDetector();
					Reader reader = cd.getReader(in, encoding);
					return QrONEUtils.read(reader);
				}catch(IndexOutOfBoundsException e){}
			}
		}
		
		return new String(QrONEUtils.read(in));
	}
	
	public Map<String,String> getHeaders() {
		return headers;
	}
	public int getResponseCode() {
		return responseCode;
	}
}
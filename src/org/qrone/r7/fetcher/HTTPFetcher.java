package org.qrone.r7.fetcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Map;

import org.qrone.util.QrONEUtils;

import com.ibm.icu.text.CharsetDetector;

public abstract class HTTPFetcher {

	public abstract HTTPResponse request(HTTPRequest request) throws IOException;
	
	public HTTPResponse request(
			String url, String method, byte[] body, 
			Map<String, String> headers,
			boolean followRedirect
				) throws IOException{
		HTTPRequest r = new HTTPRequest(
				new URL(url),method,body,headers,followRedirect);
		return request(r);
	}
	
	public InputStream fetch(String url) throws IOException{
		return request(url, "GET", null, null, true).getInputStream();
	}

	public InputStream fetch(String url, Map<String, String> headers) throws IOException{
		return request(url, "GET", null, headers, true).getInputStream();
	}
	
	public InputStream fetch(String url, byte[] body) throws IOException{
		return request(url, "POST", body, null, true).getInputStream();
	}

	public InputStream fetch(String url, byte[] body, Map<String, String> headers) throws IOException{
		return request(url, "POST", body, headers, true).getInputStream();
	}

	public String get(String url) throws IOException{
		HTTPResponse res = request(url,"GET",null,null,true);
		String contentType = res.getHeaders().get("Content-Type");
		String encoding = null;
		int idx = contentType.indexOf("charset=");
		if(idx >= 0){
			encoding = contentType.substring(idx);
		}
		
		CharsetDetector cd = new CharsetDetector();
		Reader reader = cd.getReader(res.getInputStream(), encoding);
		return QrONEUtils.read(reader);
		
	}
	
	public String post(String url, String body) throws IOException{
		HTTPResponse res = request(url,"POST",body.getBytes(),null,true);
		String contentType = res.getHeaders().get("Content-Type");
		String encoding = null;
		int idx = contentType.indexOf("charset=");
		if(idx >= 0){
			encoding = contentType.substring(idx);
		}
		
		CharsetDetector cd = new CharsetDetector();
		Reader reader = cd.getReader(res.getInputStream(), encoding);
		return QrONEUtils.read(reader);
	}
}

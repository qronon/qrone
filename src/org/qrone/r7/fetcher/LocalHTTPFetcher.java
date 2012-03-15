package org.qrone.r7.fetcher;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

public class LocalHTTPFetcher extends HTTPFetcher{
	private HttpClient c
	public LocalHTTPFetcher(){
		c = new DefaultHttpClient();
	}
	
	@Override
	public HTTPResponse request(HTTPRequest request) throws IOException {
		// TODO Auto-generated method stub
		HttpGet re
		if(request.getMethod())
		HttpGet request = new HttpGet()
		
		c.execute(arg0)
		return null;
	}

}

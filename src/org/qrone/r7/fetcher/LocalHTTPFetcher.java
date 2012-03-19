package org.qrone.r7.fetcher;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

public class LocalHTTPFetcher extends HTTPFetcher{
	private HttpClient c;
	public LocalHTTPFetcher(){
		ClientConnectionManager cm = new ThreadSafeClientConnManager();
		c = new DefaultHttpClient(cm);
	}
	
	@Override
	public HTTPResponse request(HTTPRequest request) throws IOException {
		HttpUriRequest r = null;
		URL url = request.getURL();
		
		try{
			if(request.getMethod().toUpperCase().equals("GET")){
				r = new HttpGet(url.toURI());
			}else{
				HttpPost rp = new HttpPost(url.toURI());
				rp.setEntity(new ByteArrayEntity(request.getPayload()));
				r = rp;
				
			}
		}catch (URISyntaxException e) {
			throw new IOException(e);
		}
		
		Map<String,String> map = request.getHeaders();
		if(map != null){
			for (Iterator<Map.Entry<String, String>> iter = map.entrySet().iterator(); iter.hasNext();) {
				Map.Entry<String, String> entry = iter.next();
				r.setHeader(entry.getKey(), entry.getValue());
			}
		}
		
		HttpParams params = new BasicHttpParams();
		params.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, request.isFollowRedirect());
		r.setParams(params);
		
		HttpResponse res = c.execute(r);
		
		
		map = new HashMap<String, String>();
		Header[] heads = res.getAllHeaders();
		for (int i = 0; i < heads.length; i++) {
			map.put(heads[i].getName(), heads[i].getValue());
		}
		
		return new HTTPResponse(res.getEntity().getContent(), map, res.getStatusLine().getStatusCode());
	}

}

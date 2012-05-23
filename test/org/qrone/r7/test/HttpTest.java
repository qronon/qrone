package org.qrone.r7.test;

import java.io.IOException;
import java.util.Map;

import net.arnx.jsonic.JSON;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.eclipse.jetty.util.ajax.JSONObjectConvertor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qrone.r7.app.QrONEApp;
import org.qrone.util.Stream;

import static org.junit.Assert.*;

public class HttpTest {
	private static QrONEApp app;
	private static HttpClient c;
	
	@BeforeClass
	public static void setUp(){
		ClientConnectionManager cm = new ThreadSafeClientConnManager();
		c = new DefaultHttpClient(cm);
		app = new QrONEApp(9601, 9699, "./htdocs");
		app.start();
	}

	@AfterClass
	public static void tearDown(){
		app.stop();
	}
	
	@Test
	public void testHelloFile(){
		Map map = fetchJSON("/test/hello.json");
		assertEquals("OK", map.get("status"));
	}

	@Test
	public void testHelloJS(){
		Map map;
		map = fetchJSON("/test/hello.server.js");
		assertEquals("OK", map.get("status"));

		map = fetchJSON("/test/hello");
		assertEquals("OK", map.get("status"));
	}

	@Test
	public void testUserJS(){
		Map map;
		map = fetchJSON("/test/user");
		assertEquals("OK", map.get("status"));
	}
	
	public Map fetchJSON(String path){
		HttpGet r = new HttpGet("http://localhost:9601" + path);

		HttpParams params = new BasicHttpParams();
		params.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
		r.setParams(params);
		
		try {
			HttpResponse res = c.execute(r);
			
			String body = new String(Stream.read(res.getEntity().getContent()),"utf8");
			System.out.println(body);
			
			return JSON.decode(body);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		return null;
		
	}
}

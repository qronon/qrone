package org.qrone.r7.test;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import static org.junit.Assert.*;

public class EditorTest {
	private static QrONEApp app;
	private static HttpClient c;
	private static DocumentBuilder db;
	
	@BeforeClass
	public static void beforeClass(){
		app = new QrONEApp(9601, 9699, "../qrone-admintool/htdocs");
		app.start();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {}
	}

	@Before
	public void before(){
		ClientConnectionManager cm = new ThreadSafeClientConnManager();
		c = new DefaultHttpClient(cm);
	}

	@AfterClass
	public static void afterClass(){
		app.stop();
	}
	
	@Test
	public void testHelloFile(){
		Map map;
		Document doc;

		map = fetchJSON("/admin/ide/ticket");
		assertEquals("OK", map.get("status"));
		String ticket = map.get("ticket").toString();
		
		doc = fetchXML("/admin/ide/api/fslist?.ticket=" + ticket);
		//assertEquals("OK", map.get("status"));
	}
	
	public Document fetchXML(String path){
		HttpGet r = new HttpGet("http://localhost:9601" + path);

		HttpParams params = new BasicHttpParams();
		params.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
		r.setParams(params);
		
		try {
			HttpResponse res = c.execute(r);
			String body = new String(Stream.read(res.getEntity().getContent()),"utf8");
			System.out.println(body);
			return db.parse(new InputSource(new StringReader(body)));
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
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

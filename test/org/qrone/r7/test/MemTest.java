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

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

import static org.junit.Assert.*;

public class MemTest {
	
	private static SockIOPool pool;
	
	@Test
	public void testMemRaw(){
		if (pool == null) {
			String[] serverlist = new String[1];
			serverlist[0] = "localhost:11211";
			SockIOPool pool = SockIOPool.getInstance();
			pool.setServers(serverlist);
			pool.initialize();
		}
		MemCachedClient client = new MemCachedClient();
		
		client.set("test", "test9");
		
		assertEquals("test9", client.get("test"));
	}

}

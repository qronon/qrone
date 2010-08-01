package org.qrone.r7.script;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.qrone.kvs.KVSService;
import org.qrone.kvs.MongoService;
import org.qrone.memcached.ClientMemcachedService;
import org.qrone.memcached.MemcachedService;

import com.mongodb.MongoException;


public class LocalWindow extends JSObject{
	
	public LocalWindow(ServletScope ss) throws IOException{
		super(ss);
	}
	
	
	public String fetch(String url) throws IOException{
		return null;
	}
	
	public MemcachedService memcached_connect(String host, Number port){
		String[] serverlist = new String[1];
		serverlist[0] = host + ":" + String.valueOf(port);
		return new ClientMemcachedService(serverlist);
	}

	public KVSService mongo_connect(String host, Number port, String schema)
			throws UnknownHostException, MongoException{
        return new MongoService(ss, host, port, schema);
	}
	
	public KVSService mongo_connect(String host, Number port, String schema, 
			String user, String password) throws UnknownHostException, MongoException{
        return new MongoService(ss, host, port, schema, user, password);
	}

	public Connection jdbc_connect(String cls, String url) throws SQLException{
		try {
			Class.forName(cls).newInstance();
		} catch (Exception e) {
			return null;
		}
		
		return DriverManager.getConnection(url);
	}
	
	public Connection jdbc_connect(String cls, String url, Properties info) throws SQLException{
		try {
			Class.forName(cls).newInstance();
		} catch (Exception e) {
			return null;
		}
		
		return DriverManager.getConnection(url, info);
	}
	
	public Connection derby_connect(String file, 
			String user, String password) throws SQLException{
		Properties props = new Properties();
        props.put("user", user);
        props.put("password", password);
        
		return jdbc_connect("org.apache.derby.jdbc.EmbeddedDriver", 
				"jdbc:derby:" + file + ";create=true", props);
	}
	
	public Connection mysql_connect(String host, String schema, 
			String user, String password) throws SQLException{
		Properties props = new Properties();		
        props.put("user", user);		
        props.put("password", password);	
        props.put("useUnicode", "true");	
        props.put("characterEncoding", "utf8");		

		return jdbc_connect("com.mysql.jdbc.Driver", 
				"jdbc:mysql://" + host + "/" + schema, props);
	}
}

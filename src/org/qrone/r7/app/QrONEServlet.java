package org.qrone.r7.app;

import java.net.UnknownHostException;

import org.qrone.kvs.LocalKeyValueStoreService;
import org.qrone.memcached.LocalMemcachedService;
import org.qrone.memcached.MemcachedService;
import org.qrone.mongo.MongoDatabaseService;
import org.qrone.r7.PortingService;
import org.qrone.r7.PortingServlet;
import org.qrone.r7.fetcher.LocalHTTPFetcher;

import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class QrONEServlet extends PortingServlet {

	public QrONEServlet() {
		
		boolean appengine = false;
		

		PortingService services = new PortingService();
		//services.setKeyValueStoreService(new AppEngineKVSService());
		
		
		services.setURLFetcher(new LocalHTTPFetcher());
		
		try {
			MongoDatabaseService mongo = new MongoDatabaseService(new Mongo("localhost").getDB("qrone"));
			services.setDatabaseService(mongo);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String[] memcachedServer = {"localhost"};
		services.setMemcachedService(new LocalMemcachedService(memcachedServer));
		

		services.setKeyValueStoreService(
				new LocalKeyValueStoreService(services.getDatabaseService(), 
						services.getMemcachedService()));
		
		services.setLoginService(null); // TODO OpenIDHandler を入れる。
		services.setTaskManagerService(null); // TODO TaskManager unimplemented!
		
		setPortingService(services);
	}

}

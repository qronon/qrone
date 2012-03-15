package org.qrone.r7.app;

import org.qrone.r7.PortingService;
import org.qrone.r7.PortingServlet;

public class QrONEServlet extends PortingServlet {

	public QrONEServlet() {

		PortingService services = new PortingService();
		services.setKeyValueStoreService(new AppEngineKVSService());
		services.setURLFetcher(new AppEngineHTTPFetcher());
		services.setDatabaseService(new AppEngineDatastoreService());
		services.setMemcachedService(new AppEngineMemcachedService());
		services.setLoginService(null); // TODO OpenIDHandler Çì¸ÇÍÇÈÅB
		services.setTaskManagerService(null); // TODO TaskManager unimplemented!
		
		setPortingService(services);
	}

}

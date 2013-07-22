package org.qrone.r7.app;

import jargs.gnu.CmdLineParser;

import java.io.File;
import java.net.URI;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.GzipFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.qrone.r7.PortingService;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.resolver.URIResolver.Listener;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

public class QrONEApp {
	private static Logger log = LoggerFactory.getLogger(QrONEApp.class);
			
	private int port;
	private int mport;
	private String path;
	private Server server = null;
	private QrONEServlet servlet;
	
	public QrONEApp(int port, int mport){
		this.port = port;
		this.mport = mport;
		this.path = path;

		servlet = new QrONEServlet();
		
		server = new Server();
		
		Connector connector = new SelectChannelConnector();
		connector.setPort(port);
        server.addConnector(connector);
        
		FilterHolder gzip = new FilterHolder(new GzipFilter());
        gzip.setAsyncSupported(true);
        gzip.setInitParameter("mimeTypes", "text/html,text/plain,text/xml,text/javascript,text/css,application/javascript,image/svg+xml");
		gzip.setInitParameter("minGzipSize","256");
		EnumSet<DispatcherType> all = EnumSet.of(DispatcherType.ASYNC, DispatcherType.ERROR, DispatcherType.FORWARD,
	            DispatcherType.INCLUDE, DispatcherType.REQUEST);
		
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.addServlet(new ServletHolder(servlet), "/*");
		context.addFilter(gzip, "/*", all);
		server.setHandler(context);
		
	}
	
	public void startWithWindow(){
		start();
		showDisplay();
	}

	public void startAndWait(){
    	log.info("Starting QrONE Server.");
		try {
            try {
    			server.start();
    			server.join();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public void start(){
    	log.info("Starting QrONE Server.");
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
	    			server.start();
	    			server.join();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		t.start();
	}
	
	public void stop(){
		try {
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showDisplay(){
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.setText("QrONE JavaScript Server");
		shell.setSize((int) (240 * 1.618), 280);
		final Browser browser;
		try {
			browser = new Browser(shell, SWT.NONE);
		} catch (SWTError e) {
			System.out.println("Could not instantiate Browser: "
					+ e.getMessage());
			display.dispose();
			return;
		}
		
		shell.open();
		browser.setUrl("http://localhost:9601/system/resource/index");
		browser.addLocationListener(new LocationListener() {
			@Override
			public void changing(LocationEvent event) {
				if(event.location.equals("http://localhost:9601/system/resource/index")){
				}else if(event.location.equals("qrone-server:home")){
					Program.launch("file:///" + new File(".").getAbsoluteFile()
							.getParentFile().getAbsolutePath());
					event.doit = false;
				}else if(event.location.equals("qrone-server:clean")){
					servlet.clean();
					event.doit = false;
				}else{
					Program.launch(event.location);
					event.doit = false;
				}
			}

			@Override
			public void changed(LocationEvent event) {
			}
		});
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	public static void main(String[] args) {
        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option consoleOpt     = parser.addBooleanOption('c', "console");
        CmdLineParser.Option helpOpt     = parser.addBooleanOption('h', "help");
        CmdLineParser.Option verboseOpt  = parser.addBooleanOption('v', "verbose");
        CmdLineParser.Option winOpt      = parser.addBooleanOption('w', "win");
        CmdLineParser.Option portOpt     = parser.addIntegerOption('p', "port");
        CmdLineParser.Option mportOpt     = parser.addIntegerOption('m', "mport");
        CmdLineParser.Option dirOpt     = parser.addStringOption('d', "dir");

        try {
			parser.parse(args);
		} catch (CmdLineParser.OptionException e) {
            usage();
            System.exit(1);
        }
        
        Boolean help = (Boolean) parser.getOptionValue(helpOpt);
        if(help != null && help.booleanValue()){
        	usage();
            System.exit(0);
        }
        
        Integer port = (Integer)parser.getOptionValue(portOpt);
        if(port == null){
        	port = 9601;
        }

        Integer mport = (Integer)parser.getOptionValue(mportOpt);
        if(mport == null){
        	mport = 9699;
        }


        Boolean verbose = (Boolean) parser.getOptionValue(verboseOpt);
        if(verbose != null && verbose.booleanValue()){
        	setLogLevel(Level.DEBUG);
        	log.info("Setting LogLevel to DEBUG.");
        }else{
        	setLogLevel(Level.INFO);
        	log.info("Setting LogLevel to INFO.");
        }
        
        QrONEApp app = new QrONEApp(port, mport);

        String path = (String) parser.getOptionValue(dirOpt);
        if(path != null){
        	app.setHtdocsPath(path);
        }
        

        Boolean win = (Boolean) parser.getOptionValue(winOpt);
        if(win == null || !win.booleanValue()){
        	app.start();
        	app.stop();
        }else{
        	app.startWithWindow();
        	app.stop();
        }
	}
	
    public static void setLogLevel(Level ll) {
    	ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
    	root.setLevel(ll);
    	
    	if(ll.equals(Level.DEBUG))
    		root.getLoggerContext().addTurboFilter(new QrONEAppLogTurboFilter());
	}

	public void setHtdocsPath(String path) {
        servlet.setLocalFilePath(path);
	}

	private static void usage() {
        System.out.println(
                "\nUsage: java -jar qrone-x.y.z.jar [options] <file>\n\n"

                        + "Options\n"
                        + "  -h, --help                Displays this information\n"
                        + "  -v, --verbose             Display informational messages and warnings\n"
                        + "\n"
                        + "  -w, --win                 Launch Webserver in Window mode\n"
                        + "  -p, --port <port>         Set port (default 9601)\n"
                        
                        );
    }
}
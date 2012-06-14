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

public class QrONEApp {
	private int port;
	private int mport;
	private String path;
	private Server server = null;
	private QrONEServlet servlet;
	
	public QrONEApp(int port, int mport, String path){
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
		
        if(path != null){
        	servlet.setLocalFilePath(path);
        }
	}
	
	public void start(){
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

	public void startAndWait(boolean window){

		try {
            if(!window){
            	try {
	    			server.start();
	    			server.join();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
            }else{
            	start();
    			showDisplay();
            }
		} catch (Exception e1) {
			e1.printStackTrace();
		}
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
        CmdLineParser.Option helpOpt     = parser.addBooleanOption('h', "help");
        CmdLineParser.Option verboseOpt  = parser.addBooleanOption('v', "verbose");
        CmdLineParser.Option cliOpt      = parser.addBooleanOption('c', "cli");
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
        
        String path = (String) parser.getOptionValue(dirOpt);
        
        QrONEApp app = new QrONEApp(port, mport, path);

        Boolean cli = (Boolean) parser.getOptionValue(cliOpt);
        if(cli == null || !cli.booleanValue()){
        	app.startAndWait(true);
        	app.stop();
        }else{
        	app.startAndWait(false);
        	app.stop();
        }
	}
	
    private static void usage() {
        System.out.println(
                "\nUsage: java -jar qrone-x.y.z.jar [options] <file>\n\n"

                        + "Options\n"
                        + "  -h, --help                Displays this information\n"
                        + "  -v, --verbose             Display informational messages and warnings\n"
                        + "\n"
                        + "  -c, --cli                 Launch Webserver in CLI mode\n"
                        + "  -p, --port <port>         Set port (default 9601)\n"
                        
                        );
    }
}
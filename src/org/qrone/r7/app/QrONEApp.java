package org.qrone.r7.app;

import jargs.gnu.CmdLineParser;

import java.io.File;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class QrONEApp {
	private static Server server = null;

	public static void main(String[] args) {
		

    	long timer = System.currentTimeMillis();

        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option helpOpt     = parser.addBooleanOption('h', "help");
        CmdLineParser.Option verboseOpt  = parser.addBooleanOption('v', "verbose");
        CmdLineParser.Option cliOpt      = parser.addBooleanOption('c', "cli");
        CmdLineParser.Option portOpt     = parser.addIntegerOption('p', "port");

        try {
			parser.parse(args);
		} catch (CmdLineParser.OptionException e) {
            usage();
            System.exit(1);
        }
        
        
		server = new Server();
		Connector connector = new SelectChannelConnector();

        Integer port = (Integer)parser.getOptionValue(portOpt);
        if(port != null){
        	connector.setPort(port);
        }else{
        	connector.setPort(9601);
        }
        server.addConnector(connector);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		//FilterHolder gzip = handler.addFilterWithMapping(GzipFilter.class,"/*",0);
        //gzip.setAsyncSupported(true);
        //gzip.setInitParameter("minGzipSize","256");
        
		context.addServlet(new ServletHolder(new QrONEServlet()), "/*");
		server.setHandler(context);

		

        Boolean help = (Boolean) parser.getOptionValue(cliOpt);
        if(help == null || !help.booleanValue()){

    		Runnable runnable = new Runnable() {
    			@Override
    			public void run() {
    				try {
    					server.start();
    					server.join();
    				} catch (Exception e1) {
    					e1.printStackTrace();
    				}
    			}
    		};
    		Thread jettyThread = new Thread(runnable);
    		jettyThread.start();
    		
			Display display = new Display();
			Shell shell = new Shell(display);
			shell.setLayout(new FillLayout());
			shell.setText("QrONE JavaScript Server");
			shell.setSize((int) (240 * 1.618), 240);
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
					if(event.location.equals("http://qrone-server-home/")){
						Program.launch("file:///" + new File(".").getAbsoluteFile()
								.getParentFile().getAbsolutePath());
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
	
			try {
				server.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
        }else{
			try {
				server.start();
				server.join();
				server.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
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
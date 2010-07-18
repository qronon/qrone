package org.qrone.r7.app;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.r7.handler.ImageHandler;
import org.qrone.r7.handler.Scale9Handler;
import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.HTML5Element;
import org.qrone.r7.parser.HTML5OM;
import org.qrone.r7.parser.HTML5Template;
import org.qrone.r7.parser.NodeLister;

/**
 * Servlet implementation class QrONEServer
 */
public class QrONEServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static QrONEURIResolver resolver = new QrONEURIResolver(new File("."));
	private static HTML5Deck deck = new HTML5Deck(resolver);
	
	static{
		deck.addTagHandler(new Scale9Handler(deck));
    	deck.addTagHandler(new ImageHandler(deck));
	}
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QrONEServer() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			URI uri = new URI(request.getPathInfo().substring(1));
			deck.update(uri);
			
			if(uri.toString().equals("qrone-server/index.html")){
				Writer out = response.getWriter();
				HTML5OM om = deck.compile(uri);
				if(om != null){
					HTML5Template t = new HTML5Template(om);
					final File root = new File(".").getAbsoluteFile().getParentFile();
					t.set("#homepath", root.getAbsolutePath());
					t.set("#files", new NodeLister() {
						@Override
						public void accept(HTML5Template t, HTML5Element e) {
							File[] list = root.listFiles();
							for (int i = 0; i < list.length; i++) {
								t.set("#file", list[i].getName());
								t.visit(e);
							}
						}
					});
					deck.getSpriter().create();
					
					out.append(t.output());
					out.flush();
					out.close();
				}else{
					response.sendError(404);
				}
				
			}else if(resolver.hasBuffer(uri)){
				OutputStream out = response.getOutputStream();
				resolver.writeTo(uri, response.getOutputStream());
				out.flush();
				out.close();
			}else{
				Writer out = response.getWriter();
				HTML5OM om = deck.compile(uri);
				if(om != null){
					deck.getSpriter().create();
					
					out.append(om.serialize());
					out.flush();
					out.close();
					
				}else{
					response.sendError(404);
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e){
			
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}

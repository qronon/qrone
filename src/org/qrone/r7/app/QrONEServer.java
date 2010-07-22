package org.qrone.r7.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.r7.QrONEUtils;
import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.HTML5Element;
import org.qrone.r7.parser.HTML5OM;
import org.qrone.r7.parser.HTML5Template;
import org.qrone.r7.parser.NodeLister;
import org.qrone.r7.resolver.CascadeResolver;
import org.qrone.r7.resolver.FileResolver;
import org.qrone.r7.resolver.FilteredResolver;
import org.qrone.r7.resolver.MemoryResolver;
import org.qrone.r7.resolver.QrONEResolver;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.script.ScriptDeck;
import org.qrone.r7.script.ScriptOM;
import org.qrone.r7.tag.ImageHandler;
import org.qrone.r7.tag.Scale9Handler;

/**
 * Servlet implementation class QrONEServer
 */
public class QrONEServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static CascadeResolver resolver;
	private static HTML5Deck deck;
	private static ScriptDeck vm;
	
	static{
		resolver = new CascadeResolver();
		resolver.add(new FilteredResolver("qrone-server/", new QrONEResolver()));
		resolver.add(new MemoryResolver());
		resolver.add(new FileResolver(new File(".")));
		
		deck = new HTML5Deck(resolver);
		deck.addTagHandler(new Scale9Handler(deck));
    	deck.addTagHandler(new ImageHandler(deck));
    	
    	vm = new ScriptDeck(resolver);
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
			URI uri = new URI("index.html").resolve(
					new URI(request.getPathInfo().substring(1)));
			String uristr = uri.toString();
			
			String[] paths = uristr.split("/");
			String path = "";
			String args = "";
			for (int i = 0; i < paths.length; i++) {
				path += paths[i];
				
				if(resolver.exist(path + ".js")){
					path += ".js";
					uri = new URI(path);
					uristr = path;
					
					if(paths.length > i+1){
						for (int j = i+1; j < paths.length; j++) {
							args += "/" + paths[j];
						}
					}
					
					ScriptOM om = vm.compile(uri);
					if(om != null){
						om.run(request, response);
					}else{
						response.sendError(404);
					}
					
					return;
				}
				
				path += "/";
			}
			
			
			if(uristr.equals("qrone-server/index.html")){
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
					
					out.append(t.output());
					deck.getSpriter().create();
					out.flush();
					out.close();
				}else{
					response.sendError(404);
				}
				
			}else if(resolver.exist(uristr)){
				deck.update(uri);
				
				InputStream in = resolver.getInputStream(uri);
				OutputStream out = response.getOutputStream();
				QrONEUtils.copy(in, out);
				out.flush();
				out.close();
				in.close();
			}else{
				
				
				if(resolver.exist(uri.toString())){
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

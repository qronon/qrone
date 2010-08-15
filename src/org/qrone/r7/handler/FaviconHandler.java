package org.qrone.r7.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.r7.resolver.URIResolver;
import org.qrone.util.QrONEUtils;

public class FaviconHandler implements URIHandler{
	private URIResolver resolver;
	public FaviconHandler(URIResolver resolver) {
		this.resolver = resolver;
	}
	
	@Override
	public boolean handle(HttpServletRequest request,
			HttpServletResponse response, String path, String pathArg) {
		if(path.equals("/favicon.ico")){
			try {
				InputStream in;
				OutputStream out;
				if(resolver.exist(path)){
					in = resolver.getInputStream(new URI(path));
					out = response.getOutputStream();
					QrONEUtils.copy(in, out);
					in.close();
					out.flush();
					out.close();
					
				}else{
					in = QrONEUtils.getResourceAsStream("/favicon.ico");
					out = response.getOutputStream();
					QrONEUtils.copy(in, out);
					in.close();
					out.flush();
					out.close();
				}
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}

package org.qrone.r7.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.r7.QrONEUtils;
import org.qrone.r7.resolver.URIResolver;

public class ResolverHandler implements URIHandler{
	private URIResolver resolver;
	
	public ResolverHandler(URIResolver resolver) {
		this.resolver = resolver;
	}

	@Override
	public boolean handle(HttpServletRequest request, HttpServletResponse response, String path){
		InputStream in = null;
		try{
			URI uri = new URI(request.getPathInfo());
			if(resolver.exist(path)){
				in = resolver.getInputStream(uri);
				OutputStream out = response.getOutputStream();
				QrONEUtils.copy(in, out);
				out.flush();
				out.close();
				return true;
			}
		}catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return false;
	}
}

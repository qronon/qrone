package org.qrone.r7.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
	public boolean handle(HttpServletRequest request, HttpServletResponse response, 
			String path, String pathArg){
		InputStream in = null;
		try{
			URI uri = new URI(request.getPathInfo());
			if(resolver.exist(path)){
				byte[] buf = QrONEUtils.read(resolver.getInputStream(uri));
				String str = QrONEUtils.base64_encode(MessageDigest.getInstance("SHA-1").digest(buf));
				String etag = request.getHeader("If-None-Match");
				if(etag != null && etag.equals(str.trim())){
					response.setStatus(304);
					return true;
				}
				response.setHeader("ETag", str.trim());
				OutputStream out = response.getOutputStream();
				out.write(buf);
				out.flush();
				out.close();
				return true;
			}
		}catch (IOException e) {
			e.printStackTrace();
		}catch (URISyntaxException e) {
			e.printStackTrace();
		}catch (NoSuchAlgorithmException e) {
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

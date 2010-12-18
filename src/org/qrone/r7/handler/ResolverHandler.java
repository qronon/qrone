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

import org.qrone.r7.resolver.URIResolver;
import org.qrone.util.MimeTypeParser;
import org.qrone.util.QrONEUtils;

public class ResolverHandler implements URIHandler{
	private MimeTypeParser parser;
	private URIResolver resolver;
	
	public ResolverHandler(URIResolver resolver) {
		this.resolver = resolver;
	}

	@Override
	public boolean handle(HttpServletRequest request, HttpServletResponse response, 
			String uri, String path, String pathArg){
		InputStream in = null;
		try{
			URI urio = new URI(uri);
			if(resolver.exist(uri)){
				URI mimetype = new URI("/system/resource/mime.types");
				if(parser == null){
					parser = new MimeTypeParser();
					parser.parse(resolver.getInputStream(mimetype));
				}else{
					if(resolver.updated(mimetype)){
						parser.parse(resolver.getInputStream(mimetype));
					}
				}
				
				int eidx = uri.lastIndexOf('.');
				if(eidx >= 0){
					String mime = parser.getMimeType(uri.substring(eidx+1));
					if(mime != null){
						response.setHeader("Content-Type", mime);
					}
				}
				
				byte[] buf = QrONEUtils.read(resolver.getInputStream(urio));
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

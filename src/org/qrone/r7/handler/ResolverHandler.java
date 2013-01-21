package org.qrone.r7.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.r7.resolver.URIResolver;
import org.qrone.util.MimeTypeParser;
import org.qrone.util.QrONEUtils;
import org.qrone.util.Stream;
import org.qrone.util.Token;

public class ResolverHandler implements URIHandler{
	private MimeTypeParser parser;
	private URIResolver resolver;
	private Map<String, String> cacheMap = new HashMap<String, String>();
	
	public ResolverHandler(URIResolver resolver) {
		this.resolver = resolver;
		resolver.addUpdateListener(new URIResolver.Listener() {
			@Override
			public void update(URI uri) {
				cacheMap.remove(uri.toString());
			}
		});
	}

	@Override
	public boolean handle(HttpServletRequest request, HttpServletResponse response, 
			String uri, String path, String leftpath){
		InputStream in = null;
		try{
			URI urio = new URI(uri);
			String etag = cacheMap.get(uri);
			if(etag != null && etag.equals(request.getHeader("If-None-Match"))){
				response.setStatus(304);
				return true;
			}
			
			if(resolver.exist(uri)){
				if(etag == null){
					etag = UUID.randomUUID().toString();
					cacheMap.put(uri, etag);
				}
				
				URI mimetype = new URI("/system/resource/mime.types");
				if(parser == null){
					MimeTypeParser p = new MimeTypeParser();
					p.parse(resolver.getInputStream(mimetype));
					parser = p;
				}
				
				int eidx = uri.lastIndexOf('.');
				if(eidx >= 0){
					String mime = parser.getMimeType(uri.substring(eidx+1));
					if(mime != null){
						response.setHeader("Content-Type", mime);
					}
				}
				
				response.setHeader("ETag", etag);
				in = resolver.getInputStream(urio);
				OutputStream out = response.getOutputStream();
				Stream.copy(in, out);
				out.flush();
				out.close();
				return true;
			}
		}catch (IOException e) {
			e.printStackTrace();
		}catch (URISyntaxException e) {
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

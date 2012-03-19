package org.qrone.img;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Map;
import java.util.WeakHashMap;

import org.qrone.r7.resolver.URIResolver;

public class ImageSpriteService implements URIResolver{
	private URIResolver resolver;
	private URIResolver cache;
	private ImageBufferService service;
	private Map<String, ImageSprite> map = new Hashtable<String, ImageSprite>();
	private Map<URI, ImageSize> smap = new Hashtable<URI, ImageSize>();
	private Map<URI, ImageBuffer> imap = new WeakHashMap<URI, ImageBuffer>();
	private URI dot;
	
	public ImageSpriteService(URIResolver resolver, URIResolver cache, ImageBufferService service) {
		this.resolver = resolver;
		this.cache = cache;
		this.service = service;
	}

	public URI addTransparentDot() throws IOException {
		try {
			return new URI("/system/resource/1dot.png");
		} catch (URISyntaxException e) {
			throw new IOException();
		}
	}
	
	public String getStyle(ImagePart imagePart) {
		String uri = getURI(imagePart);
		ImageSprite sprite = map.get(uri);
		if(sprite == null){
			sprite = new ImageSprite(this,imagePart.type,uri);
			map.put(uri, sprite);
		}
		return sprite.getStyle(imagePart);
	}
	
	public ImageSize getImageSize(URI file) throws IOException{
		if(smap.containsKey(file)){
			return smap.get(file);
		}else{
			ImageBuffer buf = getImage(file);
			ImageSize size = new ImageSize(buf.getWidth(), buf.getHeight());
			smap.put(file, size);
			return size;
		}
	}
	
	private String getURI(ImagePart part){
		String uri = part.file.toString();
		int idx = uri.lastIndexOf('/');
		if(idx >= 0){
			uri = uri.substring(0,idx);
			
			switch(part.type){
			case REPEAT_X:
				return "/system/sprite" + uri + "/xsprite.png";
			case REPEAT_Y:
				return "/system/sprite" + uri + "/ysprite.png";
			case SINGLE:
				return "/system/sprite" + uri + "/sprite.png";
			}
		}
		return null;
	}
	
	public ImageBuffer getImage(URI file) throws IOException{
		ImageBuffer i = imap.get(file);
		if(i != null){
			return i;
		}else{
			InputStream in = resolver.getInputStream(file);
			if(in != null){
				try{
					i = service.createImage(in);
					imap.put(file, i);
					
					ImageSize size = new ImageSize(i.getWidth(), i.getHeight());
					smap.put(file, size);
					return i;
				}finally{
					in.close();
				}
			}
		}
		return null;
	}

	public ImageBuffer createImage(int width, int height) {
		return service.createImage(width, height);
	}

	@Override
	public boolean exist(String path) {
		return map.containsKey(path);
	}

	@Override
	public boolean remove(URI uri) {
		return false;
	}

	@Override
	public InputStream getInputStream(URI uri) throws IOException {
		try{
			InputStream in = cache.getInputStream(uri);
			if(in != null){
				return in;
			}
			
			ImageSprite sprite = map.get(uri.toString());
			if(sprite != null){
				OutputStream out = cache.getOutputStream(uri);
				sprite.writeTo(out);
				
				return sprite.getInputStream();
			}
		}catch(IOException e){
		}
		return null;
	}

	@Override
	public OutputStream getOutputStream(URI uri) throws IOException {
		return null;
	}
	


	@Override
	public void addUpdateListener(Listener l) {
	}
	
}

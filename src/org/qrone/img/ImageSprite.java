package org.qrone.img;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.qrone.img.ImagePart.TYPE;
import org.qrone.util.Digest;
import org.qrone.util.QrONEUtils;

public class ImageSprite {
	private ImageSpriteService service;
	private TYPE type;
	private String uri;
	
	private List<ImagePart> parts = new ArrayList<ImagePart>();
	private String sha;
	private Map<ImagePart, Integer> offsetCache = new Hashtable<ImagePart, Integer>();

	private int width = 0;
	private int height = 0;
	
	public ImageSprite(ImageSpriteService service, TYPE type, String uri) {
		this.service = service;
		this.type = type;
		this.uri = uri;
	}
	
	public int size() {
		return parts.size();
	}
	
	public InputStream getInputStream() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writeTo(out);
		sha = Digest.sha1(out.toByteArray());
		return new ByteArrayInputStream(out.toByteArray());
	}

	public void writeTo(OutputStream out) throws IOException {
		switch (type) {
		case REPEAT_X:
			createh(out);
			break;
		case REPEAT_Y:
			createv(out);
			break;
		case SINGLE:
			createi(out);
			break;
		}
	}

	public void createi(OutputStream out) throws IOException {
		int currentY = 0;
		int currentX = 0;
		
		ImageBuffer iimage = service.createImage(width, height);
		currentY = 0;
		for (Iterator<ImagePart> i = parts.iterator(); i
				.hasNext();) {
			ImagePart part = i.next();
			iimage.drawImage(service.getImage(part.file), 
					new ImageRect(currentX, currentY, part.w, part.h), 
					new ImageRect(part.x, part.y, part.w, part.h));
			currentY += part.h;
		}
		iimage.writeTo(out);
	}
	
	private void createv(OutputStream out) throws IOException {
		int currentY = 0;
		int currentX = 0;
		
		ImageBuffer vimage = service.createImage(width, height);
		currentX = 0;
		for (Iterator<ImagePart> i = parts.iterator(); i.hasNext();) {
			ImagePart part = i.next();

			currentY = 0;
			ImageBuffer is = service.getImage(part.file);
			while (currentY < height) {
				vimage.drawImage(is, 
						new ImageRect(currentX, currentY, part.w, part.h), 
						new ImageRect(part.x, part.y, part.w, part.h));
				currentY += part.h;
			}

			currentX += part.w;
		}
		vimage.writeTo(out);
	}
	
	private void createh(OutputStream out) throws IOException {
		int currentY = 0;
		int currentX = 0;
		
		ImageBuffer himage = service.createImage(width, height);
		currentY = 0;
		for (Iterator<ImagePart> i = parts.iterator(); i.hasNext();) {
			ImagePart part = i.next();

			currentX = 0;
			ImageBuffer is = service.getImage(part.file);
			while (currentX < width) {
				himage.drawImage(is, 
						new ImageRect(currentX, currentY, part.w, part.h), 
						new ImageRect(part.x, part.y, part.w, part.h));
				currentX += part.w;
			}

			currentY += part.h;
		}
		himage.writeTo(out);
	}
	
	public String getStyle(ImagePart part) {
		int offset = getRenderedOffset(part);
		
		switch (type) {
		case REPEAT_X:
			return ";height:" + part.h + "px;" 
			 + "background: repeat-x 0px -" + offset + "px url(" + uri + ");";
		case REPEAT_Y:
			return ";width:" + part.w + "px;" 
			 + "background: repeat-y -" + offset + "px 0px url(" + uri + ");";
		case SINGLE:
			return ";width:" + part.w + "px;"  + "height:" + part.h + "px;" 
			 + "background: no-repeat 0px -" + offset + "px url(" + uri + ");";
		}
		return null;
	}

	private int getRenderedOffset(ImagePart part) {
		Integer offset = offsetCache.get(part);
		if(offset == null){
			parts.add(part);
			int off;
			switch (type) {
			case SINGLE:
			case REPEAT_X:
				off = height;
				if(width < part.w){
					width = part.w;
				}
				height += part.h;
				offsetCache.put(part, off);
				return off;
			case REPEAT_Y:
				off = width;
				if(height < part.h){
					height = part.h;
				}
				width += part.w;
				offsetCache.put(part, off);
				return off;
			}
		}
		return offset;
	}

	public String getSHA() {
		if(sha == null){
			try {
				getInputStream();
			} catch (IOException e) {
			}
		}
		return sha;
	}

}

package org.qrone.img;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;


public class ImagePart implements Serializable{
	
	public static enum TYPE{
		REPEAT_X,
		REPEAT_Y,
		SINGLE
	}
	
	private static final long serialVersionUID = -2816750802010010337L;
	
	public URI file;
	public int x;
	public int y;
	public int w;
	public int h;
	public TYPE type;

	public ImagePart(URI f, ImageSpriteService service){
		this.file = f;
		type = TYPE.SINGLE;
		
		try {
			ImageSize size = service.getImageSize(f);
			x = 0;
			y = 0;
			w = size.w;
			h = size.h;
		} catch (IOException e) {}
	}
	
	public ImagePart(URI f, int x, int y, int w, int h, TYPE type) {
		this.file = f;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.type = type;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ImagePart)) return false;
		ImagePart p = (ImagePart)obj;
		
		if(p.file.equals(file) && p.x == x && p.y == y
				&& p.w == w && p.h == h) return true;
		return false;
	}
	
	@Override
	public int hashCode() {
		return file.hashCode() + x + y + w + h;
	}
}

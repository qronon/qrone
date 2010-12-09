package org.qrone.img;

public class ImageSize {
	public int w;
	public int h;
	
	public ImageSize(int w, int h) {
		this.w = w;
		this.h = h;
	}

	public String toString(){
		return "[" + w + "," + h + "]";
	}

}

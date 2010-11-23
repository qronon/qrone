package org.qrone.img;

public class ImageRect {
	public int x;
	public int y;
	public int w;
	public int h;
	
	public ImageRect(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public String toString(){
		return "[" + x + "," + y + "," + w + "," + h + "]";
	}
}

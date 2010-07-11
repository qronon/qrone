package org.qrone.r7;

import java.io.File;


public class PartOfImage{
	public File file;
	public int x;
	public int y;
	public int w;
	public int h;
	
	public PartOfImage(File f, int x, int y, int w, int h) {
		this.file = f;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof PartOfImage)) return false;
		PartOfImage p = (PartOfImage)obj;
		
		if(p.file.equals(file) && p.x == x && p.y == y
				&& p.w == w && p.h == h) return true;
		return false;
	}
	
	@Override
	public int hashCode() {
		return file.hashCode() + x + y + w + h;
	}
}

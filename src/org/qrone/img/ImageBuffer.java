package org.qrone.img;

import java.io.IOException;
import java.io.OutputStream;

public interface ImageBuffer {
	public int getWidth();
	public int getHeight();
	public void drawImage(ImageBuffer img, ImageRect to, ImageRect from);
	public void writeTo(OutputStream out) throws IOException;
}

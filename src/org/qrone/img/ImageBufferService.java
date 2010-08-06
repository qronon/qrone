package org.qrone.img;

import java.io.InputStream;

public interface ImageBufferService {
	public ImageBuffer createImage(int width, int height);
	public ImageBuffer createImage(InputStream in);
}

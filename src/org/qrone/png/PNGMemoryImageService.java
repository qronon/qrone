package org.qrone.png;

import java.io.InputStream;

import org.qrone.img.ImageBuffer;
import org.qrone.img.ImageBufferService;

public class PNGMemoryImageService implements ImageBufferService{

	@Override
	public ImageBuffer createImage(int width, int height) {
		return new PNGMemoryImage(width, height);
	}

	@Override
	public ImageBuffer createImage(InputStream in) {
		return new PNGMemoryImage(in);
	}

}

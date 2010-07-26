package org.qrone.img;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class AwtImageBufferService implements ImageBufferService{

	@Override
	public ImageBuffer createImage(int width, int height) {
		return new AwtImageBuffer(new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR));
	}

	@Override
	public ImageBuffer createImage(InputStream in) {
		try {
			return new AwtImageBuffer(ImageIO.read(in));
		} catch (IOException e) {
			return null;
		}
	}

}

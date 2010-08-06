package org.qrone.img;

import java.io.InputStream;

import org.qrone.r7.parser.ImagePack;
import org.qrone.r7.resolver.URIResolver;

public interface ImageBufferService {
	public ImageBuffer createImage(int width, int height);
	public ImageBuffer createImage(InputStream in);
}

package org.qrone.r7.appengine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.mortbay.resource.URLResource;
import org.qrone.img.ImageBuffer;
import org.qrone.img.ImageBufferService;
import org.qrone.r7.QrONEUtils;
import org.qrone.r7.parser.ImagePack;
import org.qrone.r7.resolver.URIResolver;

import com.google.appengine.api.images.ImagesServiceFactory;

public class AppEngineImageBufferService implements ImageBufferService{

	@Override
	public ImageBuffer createImage(int width, int height) {
		return new AppEngineImageBuffer(width, height);
	}

	@Override
	public ImageBuffer createImage(InputStream in) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			QrONEUtils.copy(in, out);
		} catch (IOException e) {
			return null;
		}
		return new AppEngineImageBuffer(
				ImagesServiceFactory.makeImage(out.toByteArray())
				);
	}

	

}
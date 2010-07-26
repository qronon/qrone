package org.qrone.img;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.google.appengine.api.images.Composite;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;

public class AppEngineImageBuffer implements ImageBuffer{
	private Image image;
	private int width;
	private int height;
	private List<Composite> composites = new ArrayList<Composite>();
	public AppEngineImageBuffer(Image image){
		this.image = image;
		width = image.getWidth();
		height = image.getHeight();
	}

	public AppEngineImageBuffer(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void drawImage(ImageBuffer img, ImageRect to, ImageRect from) {
		AppEngineImageBuffer buf = (AppEngineImageBuffer)img;
		int w = img.getWidth();
		int h = img.getHeight();
		Transform t = ImagesServiceFactory.makeCrop(
				w/from.x, h/from.y, w/(from.x+from.w), h/(from.y+from.h));
		Image i = ImagesServiceFactory.getImagesService().applyTransform(t, buf.image);
		Composite c = ImagesServiceFactory.makeComposite(i, to.x, to.y, 1, Composite.Anchor.TOP_LEFT);
		composites.add(c);
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void writeTo(OutputStream out) {
		Image i = ImagesServiceFactory.getImagesService()
			.composite(composites, width, height, 0x00000000);
		
		try {
			out.write(i.getImageData());
		} catch (IOException e) {
		}
	}
	
}

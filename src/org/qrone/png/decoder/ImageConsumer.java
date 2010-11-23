package org.qrone.png.decoder;

import java.util.Hashtable;


public interface ImageConsumer {

	public static final String IMAGEERROR = null;
	public static final String STATICIMAGEDONE = null;
	public static final int TOPDOWNLEFTRIGHT = 0;
	public static final int COMPLETESCANLINES = 0;
	public static final int SINGLEPASS = 0;
	public static final int SINGLEFRAME = 0;

	public void imageComplete(String imageerror2);
	public void setDimensions(int width, int height);
	public void setProperties(Hashtable properties);
	public void setColorModel(ColorModel model);
	public void setHints(int i);
	public void setPixels(int x, int y, int w, int h, ColorModel model,
			byte[] bpixels, int off, int dataWidth);
	public void setPixels(int x, int y, int w, int h, ColorModel model,
			int[] ipixels, int off, int dataWidth);

}

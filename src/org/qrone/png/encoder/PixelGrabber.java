package org.qrone.png.encoder;

import org.qrone.png.PNGMemoryImage;

public class PixelGrabber {
	
	public final static int ABORT = 1;
	
	private PNGMemoryImage image;
	private int width;
	private int row;
	private int nRow;
	private int[] pixels;
	private int offset = 0;

	public PixelGrabber(PNGMemoryImage image, int zero, int row, int width, int nRow,
			int[] pixels, int zero3, int width2) {
		this.image = image;
		this.width = width;
		this.row = row;
		this.nRow = nRow;
		this.pixels = pixels;
	}

	public void grabPixels() {
		int[] raw = image.getPixels();
		offset = row * width;
		int length = Math.min(width * nRow, raw.length - offset);
		for (int i = 0; i < length; i++) {
			pixels[i] = raw[i + offset];
		}
		offset += width * nRow;
	}

	public int getStatus() {
		return 0;
	}

}

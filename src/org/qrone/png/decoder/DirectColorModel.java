package org.qrone.png.decoder;


public class DirectColorModel extends ColorModel {
	private int bits;
	private int rmask;
	private int gmask;
	private int bmask;
	private int amask = -1;

	public DirectColorModel(int bits, int rmask, int gmask, int bmask) {
		this.bits = bits;
		this.rmask = rmask;
		this.gmask = gmask;
	}

	public DirectColorModel(int bits, int rmask, int gmask, int bmask, int amask) {
		this.bits = bits;
		this.rmask = rmask;
		this.gmask = gmask;
		this.amask = amask;
	}

    public int getRGB(int pixel) {
        return mask(pixel, rmask)
        	 | mask(pixel, gmask)
        	 | mask(pixel, bmask)
        	 | (amask < 0 ? 0xFF000000 : mask(pixel, amask));
    }
    
    private int mask(int pixel, int mask) {
		int off = 0;
		if (mask != 0) {
		    while ((mask & 1) == 0) {
		    	mask >>>= 1;
		    	off++;
		    }
		}
		
		return pixel & rmask >>> off;
	}
}

package org.qrone.png.decoder;

import java.math.BigInteger;

public class IndexColorModel extends ColorModel {
    private int rgb[];
    private int map_size;
    private int pixel_mask;
    private int transparent_index = -1;
    private boolean allgrayopaque;
    private BigInteger validBits;
    
    private static int[] opaqueBits = {8, 8, 8};
    private static int[] alphaBits = {8, 8, 8, 8};

    protected int pixel_bits;
    int nBits[];
    int transparency = TRANSLUCENT;
    boolean supportsAlpha = true;
    boolean isAlphaPremultiplied = false;
    int numComponents = -1;
    int numColorComponents = -1;
    int maxBits;
    boolean is_sRGB = true;
    
    /*
     * Represents image data that is guaranteed to be completely opaque,
     * meaning that all pixels have an alpha value of 1.0.
     */
    public final static int OPAQUE            = 1;
    public final static int BITMASK = 2;
    public final static int TRANSLUCENT        = 3;

    public static final int TYPE_BYTE  = 0;
    public static final int TYPE_USHORT = 1;
    public static final int TYPE_SHORT = 2;
    public static final int TYPE_INT   = 3;
    public static final int TYPE_FLOAT  = 4;
    public static final int TYPE_DOUBLE  = 5;
    public static final int TYPE_UNDEFINED = 32;
    
    
    public IndexColorModel(int bits, int size, byte cmap[], int start,
			   boolean hasalpha) {
	this(bits, size, cmap, start, hasalpha, -1);
        if (bits < 1 || bits > 16) {
            throw new IllegalArgumentException("Number of bits must be between"
                                               +" 1 and 16.");
        }
    }
    
    public IndexColorModel(int bits, int size, byte cmap[], int start,
			   boolean hasalpha, int trans) {
	// REMIND: This assumes the ordering: RGB[A]
	//super(bits, opaqueBits,
    //          ColorSpace.getInstance(ColorSpace.CS_sRGB),
    //          false, false, OPAQUE,
    //          getDefaultTransferType(bits));

        if (bits < 1 || bits > 16) {
            throw new IllegalArgumentException("Number of bits must be between"
                                               +" 1 and 16.");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Map size ("+size+
                                               ") must be >= 1");
        }
	map_size = size;
	rgb = new int[calcRealMapSize(bits, size)];
	int j = start;
	int alpha = 0xff;
	boolean allgray = true;
        int transparency = OPAQUE;
	for (int i = 0; i < size; i++) {
	    int r = cmap[j++] & 0xff;
	    int g = cmap[j++] & 0xff;
	    int b = cmap[j++] & 0xff;
	    allgray = allgray && (r == g) && (g == b);
	    if (hasalpha) {
		alpha = cmap[j++] & 0xff;
		if (alpha != 0xff) {
		    if (alpha == 0x00) {
			if (transparency == OPAQUE) {
			    transparency = BITMASK;
			}
			if (transparent_index < 0) {
			    transparent_index = i;
			}
		    } else {
			transparency = TRANSLUCENT;
		    }
		    allgray = false;
		}
	    }
	    rgb[i] = (alpha << 24) | (r << 16) | (g << 8) | b;
	}
	this.allgrayopaque = allgray;
	setTransparency(transparency);
	setTransparentPixel(trans);
        calculatePixelMask();
    }

    private final void calculatePixelMask() {
        // Note that we adjust the mask so that our masking behavior here
        // is consistent with that of our native rendering loops.
        int maskbits = pixel_bits;
        if (maskbits == 3) {
            maskbits = 4;
        } else if (maskbits > 4 && maskbits < 8) {
            maskbits = 8;
        }
        pixel_mask = (1 << maskbits) - 1;
    }
    
    private void setTransparentPixel(int trans) {
	if (trans >= 0 && trans < map_size) {
	    rgb[trans] &= 0x00ffffff;
	    transparent_index = trans;
	    allgrayopaque = false;
	    if (this.transparency == OPAQUE) {
		setTransparency(BITMASK);
	    }
	}
    }

    private void setTransparency(int transparency) {
	if (this.transparency != transparency) {
	    this.transparency = transparency;
	    if (transparency == OPAQUE) {
		supportsAlpha = false;
		numComponents = 3;
		nBits = opaqueBits;
	    } else {
		supportsAlpha = true;
		numComponents = 4;
		nBits = alphaBits;
	    }
	}
    }
    
    static int getDefaultTransferType(int pixel_bits) {
        if (pixel_bits <= 8) {
            return TYPE_BYTE;
        } else if (pixel_bits <= 16) {
            return TYPE_USHORT;
        } else if (pixel_bits <= 32) {
            return TYPE_INT;
        } else {
            return TYPE_UNDEFINED;
        }
    }
    
    private int calcRealMapSize(int bits, int size) {
    	int newSize = Math.max(1 << bits, size);
    	return Math.max(newSize, 256);
    }
    /**
     * Returns the color/alpha components of the pixel in the default
     * RGB color model format.  The pixel value is specified as an int.
     * Only the lower <em>n</em> bits of the pixel value, as specified in the
     * <a href="#index_values">class description</a> above, are used to
     * calculate the returned value.
     * The returned value is in a non pre-multiplied format.
     * @param pixel the specified pixel 
     * @return the color and alpha components of the specified pixel
     * @see ColorModel#getRGBdefault
     */
    public int getRGB(int pixel) {
        return rgb[pixel & pixel_mask];
    }

}
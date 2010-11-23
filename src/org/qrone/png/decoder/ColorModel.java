package org.qrone.png.decoder;

public class ColorModel {
	private static final ColorModel RGBColorModel = new ColorModel();

	public static ColorModel getRGBdefault() {
		return RGBColorModel;
	}

    public int getRGB(int pixel){
    	return pixel;
    }
}

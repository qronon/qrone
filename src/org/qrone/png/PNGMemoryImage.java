package org.qrone.png;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

import org.qrone.img.ImageBuffer;
import org.qrone.img.ImageRect;
import org.qrone.png.decoder.ColorModel;
import org.qrone.png.decoder.ImageConsumer;
import org.qrone.png.decoder.PNGImageProducer;
import org.qrone.png.encoder.PngEncoder;


public class PNGMemoryImage implements ImageBuffer{
	private int width;
	private int height;
	private int[] buf;

	public PNGMemoryImage(int w, int h){
		width = w;
		height = h;
		buf = new int[width * height];
	}
	
	public PNGMemoryImage(InputStream in){
		PNGImageProducer p = new PNGImageProducer(in);
		p.startProductionSync(new ImageConsumer() {
			
			@Override
			public void setProperties(Hashtable properties) {
				// DO NOTHING
			}
			
			@Override
			public void setPixels(int x, int y, int w, int h, ColorModel model,
					int[] ipixels, int off, int dataWidth) {
				PNGMemoryImage.this.setPixels( x, y, w, h, model, ipixels, off, dataWidth );
			}
			
			@Override
			public void setPixels(int x, int y, int w, int h, ColorModel model,
					byte[] bpixels, int off, int dataWidth) {
				PNGMemoryImage.this.setPixels( x, y, w, h, model, bpixels, off, dataWidth );
			}
			
			@Override
			public void setHints(int i) {
				// DO NOTHING
			}
			
			@Override
			public void setDimensions(int width, int height) {
				PNGMemoryImage.this.width = width;
				PNGMemoryImage.this.height = height;
				PNGMemoryImage.this.buf = new int[width * height];
			}
			
			@Override
			public void setColorModel(ColorModel model) {
				// DO NOTHING
			}
			
			@Override
			public void imageComplete(String imageerror2) {
				// DO NOTHING
			}
		});
	}

	private void setPixels(int x, int y, int w, int h, ColorModel model, byte[] ipixels, 
			int off, int dataWidth) {
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				buf[i * width + j] = model.getRGB( ipixels[off + i * dataWidth + j] );
			}
		}
	}
	
	private void setPixels(int x, int y, int w, int h, ColorModel model, int[] ipixels, 
			int off, int dataWidth) {
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				buf[i * width + j] = model.getRGB( ipixels[off + i * dataWidth + j] );
			}
		}
	}
	
	public void draw(PNGMemoryImage img, ImageRect to, ImageRect from){
		int[] pix = img.getPixels();
		
		int w = Math.min(to.w, from.w);
		int h = Math.min(to.h, from.h);

		if(to.w + to.x > width)
			w = width - to.x;
		if(to.h + to.y > height)
			h = height - to.y;
		
		//System.out.println(toString() + " To:" + to.toString() + " From:" + from.toString());
		
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				/*
				if(from.y * img.getWidth() + from.x + j >= pix.length){
					System.out.println("pix overflow");
				}
				if(to.y * width + to.x + j >= buf.length){
					System.out.println("buf overflow");
					System.out.println(to.y * width + to.x + j);
					System.out.println(buf.length);
				}
				*/
				
				buf[(to.y + i) * width + to.x + j]
				    = pix[(from.y + i) * img.getWidth() + from.x + j];
			}
		}
	}
	
	public String toString(){
		return "PNG(" + width + "," + height + ")";
	}
	
	public int[] getPixels(){
		return buf;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void drawImage(ImageBuffer img, ImageRect to, ImageRect from) {
		draw((PNGMemoryImage)img, to, from);
	}

	@Override
	public void writeTo(OutputStream out) throws IOException{
		PngEncoder e = new PngEncoder(this, true);
		e.setCompressionLevel(9);
		out.write(e.pngEncode(true));
		out.close();
	}

}

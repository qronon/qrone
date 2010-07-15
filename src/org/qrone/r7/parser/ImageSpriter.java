package org.qrone.r7.parser;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.qrone.r7.QrONEUtils;

public class ImageSpriter {
	private String isprite = "sprite-i.png";
	private String vsprite = "sprite-v.png";
	private String hsprite = "sprite-h.png";
	private String tsprite = "sprite-t.png";
	
	private List<ImagePart> isprites = new LinkedList<ImagePart>();
	private Map<ImagePart, String> iresults = new Hashtable<ImagePart, String>();
	private int iWidth;
	private int iHeight;
	
	private List<ImagePart> vsprites = new LinkedList<ImagePart>();
	private Map<ImagePart, String> vresults = new Hashtable<ImagePart, String>();
	private int vWidth;
	private int vHeight;
	
	private List<ImagePart> hsprites = new LinkedList<ImagePart>();
	private Map<ImagePart, String> hresults = new Hashtable<ImagePart, String>();
	private int hWidth;
	private int hHeight;
	
	private boolean useTransparentDot = false;
	
	private Map<File, BufferedImage> map = new Hashtable<File, BufferedImage>();

	private URI basedir;
	private URIResolver resolver;
	
	public ImageSpriter(URIResolver resolver) {
		this.resolver = resolver;
	}
	/*
	public static ImageSpriter instance(){
		if(ins == null) ins = new ImageSpriter();
		return ins;
	}
	*/
	public BufferedImage getImage(URI file) throws IOException{
		if(map.containsKey(file)){
			return map.get(file);
		}else{
			return ImageIO.read(resolver.getInputStream(file));
		}
	}
	
	public void setImageDir(URI imgdir){
		basedir = imgdir;
	}
	
	private URI getPath(URI from, String name){
		return from.relativize(basedir.resolve(name));
		/*
		from.resolve(name);
		if(baseurl != null){
			return baseurl;
		}
		
		try {
			return FileUtil.getRelativePath(from.getParentFile(), new File(basedir, name));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return name;
		*/
	}
	
	public void create() throws IOException {
		Graphics g;
		int currentY = 0;
		int currentX = 0;
		
		if(isprites.size() > 0){
			BufferedImage iimage = new BufferedImage(iWidth, iHeight,
					BufferedImage.TYPE_4BYTE_ABGR);
			g = iimage.getGraphics();
			currentY = 0;
			for (Iterator<ImagePart> i = isprites.iterator(); i
					.hasNext();) {
				ImagePart part = i.next();
				g.drawImage(getImage(part.file), 
						currentX, currentY, currentX + part.w, currentY + part.h, 
						part.x, part.y, part.x+part.w, part.y+part.h, null);
				currentY += part.h;
			}
			ImageIO.write(iimage, "png", resolver.getOutputStream(basedir.resolve(isprite)));
		}
		
		if(vsprites.size() > 0){
			BufferedImage vimage = new BufferedImage(vWidth, vHeight,
					BufferedImage.TYPE_4BYTE_ABGR);
			g = vimage.getGraphics();
			currentX = 0;
			for (Iterator<ImagePart> i = vsprites.iterator(); i
					.hasNext();) {
				ImagePart part = i.next();
	
				currentY = 0;
				while (currentY < vWidth) {
					if(currentY + part.h > vWidth){
						g.drawImage(getImage(part.file), 
								currentX, currentY, 
								currentX + part.w, 
								currentY + part.h - (currentY + part.h - vWidth), 
								part.x, part.y, 
								part.x+part.w, 
								part.y+part.h - (currentY + part.h - vWidth), null);
					}else{
						g.drawImage(getImage(part.file), 
								currentX, currentY, 
								currentX + part.w, 
								currentY + part.h, 
								part.x, part.y, 
								part.x+part.w, 
								part.y+part.h, null);
					}
					currentY += part.h;
				}
	
				currentX += part.w;
			}
			ImageIO.write(vimage, "png", resolver.getOutputStream(basedir.resolve(vsprite)));
		}
		
		if(hsprites.size() > 0){
			BufferedImage himage = new BufferedImage(hWidth, hHeight,
					BufferedImage.TYPE_4BYTE_ABGR);
			g = himage.getGraphics();
			currentY = 0;
			for (Iterator<ImagePart> i = hsprites.iterator(); i
					.hasNext();) {
				ImagePart part = i.next();
	
				currentX = 0;
				while (currentX < hHeight) {
					if(currentX + part.w > hHeight){
						g.drawImage(getImage(part.file), 
								currentX, currentY, 
								currentX + part.w - (currentX + part.w - hHeight), 
								currentY + part.h, 
								part.x, part.y, 
								part.x+part.w - (currentX + part.w - hHeight), 
								part.y+part.h, null);
					}else{
	
						g.drawImage(getImage(part.file), 
								currentX, currentY, 
								currentX + part.w, 
								currentY + part.h, 
								part.x, part.y, 
								part.x+part.w, 
								part.y+part.h, null);
					}
					currentX += part.w;
				}
	
				currentY += part.h;
			}
			ImageIO.write(himage, "png", resolver.getOutputStream(basedir.resolve(hsprite)));
		}
		
		if(useTransparentDot){
			InputStream in = QrONEUtils.getResourceAsStream("1dot.png");
			OutputStream out = resolver.getOutputStream(basedir.resolve(tsprite));
			int buf;
		    while ((buf = in.read()) >= 0)
		        out.write(buf);
		    in.close();
		    out.close();
		}
		
		
	}
	
	public String addISprite(URI file) throws IOException{
		BufferedImage b = getImage(file);
		isprites.add(new ImagePart(file, 0, 0, b.getWidth(), b.getHeight()));
		
		if(iWidth < b.getWidth()){
			iWidth = b.getWidth();
		}
		iHeight += b.getHeight();
		
		return "width:" + b.getWidth() + "px;" 
			 + "height:" + b.getHeight() + "px;" 
			 + "background: no-repeat 0px -" + (iHeight-b.getHeight()) + "px url(" + getPath(file, "isprite.png") + ");";
	}

	public String addISprite(ImagePart file) throws IOException{
		if(iresults.containsKey(file)){
			return iresults.get(file);
		}
		
		isprites.add(file);
		if(iWidth < file.w){
			iWidth = file.w;
		}
		iHeight += file.h;
		
		String res = "width:" + file.w + "px;" 
			 + "height:" + file.h + "px;" 
			 + "background: no-repeat 0px -" 
			 	+ (iHeight-file.h) + "px url(" + getPath(file.file, "isprite.png") + ");";
		iresults.put(file, res);
		return res;
	}
	public String addVSprite(ImagePart file) throws IOException{
		if(vresults.containsKey(file)){
			return vresults.get(file);
		}
		
		vsprites.add(file);
		if(vHeight < file.h){
			vHeight = file.h;
		}
		vWidth += file.w;
		
		String res = "width:" + file.w + "px;" 
			 + "background: repeat-y -" + (vWidth-file.w) + "px 0px url(" + getPath(file.file, "vsprite.png") + ");";
		vresults.put(file, res);
		return res;
	}

	public String addHSprite(ImagePart file) throws IOException{
		if(hresults.containsKey(file)){
			return hresults.get(file);
		}
		
		hsprites.add(file);
		if(hWidth < file.w){
			hWidth = file.w;
		}
		hHeight += file.h;
		
		String res = "height:" + file.h + "px;" 
			 + "background: repeat-x 0px -" + (hHeight-file.h) + "px url(" + getPath(file.file, "hsprite.png") + ");";
		hresults.put(file, res);
		return res;
	}
	
	public URI addTransparentDot(){
		useTransparentDot = true;
		return basedir.resolve(tsprite);
	}
	
}

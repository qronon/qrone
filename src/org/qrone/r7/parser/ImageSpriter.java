package org.qrone.r7.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.qrone.img.ImageBuffer;
import org.qrone.img.ImageBufferService;
import org.qrone.img.ImageRect;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.util.QrONEUtils;

public class ImageSpriter {
	private String isprite = "/sprite-i.png";
	private String vsprite = "/sprite-v.png";
	private String hsprite = "/sprite-h.png";
	private String tsprite = "/sprite-t.png";
	private String psprite = "/sprite-pack.ser";

	private URI ispriteURI;
	private URI vspriteURI;
	private URI hspriteURI;
	private URI tspriteURI;
	private URI pspriteURI;
	
	private ImagePack pack;
	
	private boolean useTransparentDot = false;
	private boolean outTransparentDot = false;
	
	private Map<URI, ImageBuffer> map = new Hashtable<URI, ImageBuffer>();

	private URI basedir;
	private URIResolver resolver;
	private ImageBufferService service;
	
	public ImageSpriter(URIResolver resolver, ImageBufferService service) {
		this.resolver = resolver;
		this.service = service;
		
		try {
			setBaseURI(new URI("."));
		} catch (URISyntaxException e) {}
	}

	public boolean pack() {
		try {
			return QrONEUtils.serialize(pack, resolver.getOutputStream(pspriteURI));
		} catch (IOException e) {}
		return false;
	}
	
	private ImagePack unpack() {
		if (pack == null && pspriteURI != null) {
			try {
				pack = (ImagePack) QrONEUtils.unserialize(
						resolver.getInputStream(pspriteURI));
			} catch (IOException e) {}
		}
		if(pack == null)
			pack = new ImagePack();
		return pack;
	}
	
	public ImageBuffer getImage(URI file) throws IOException{
		if(map.containsKey(file)){
			return map.get(file);
		}else{
			InputStream in = resolver.getInputStream(file);
			try{
				ImageBuffer i = service.createImage(in);
				map.put(file, i);
				return i;
			}finally{
				in.close();
			}
		}
	}
	
	public void setBaseURI(URI imgdir){
		basedir = imgdir;
		ispriteURI = basedir.resolve(isprite);
		tspriteURI = basedir.resolve(tsprite);
		vspriteURI = basedir.resolve(vsprite);
		hspriteURI = basedir.resolve(hsprite);
		pspriteURI = basedir.resolve(psprite);
	}
	
	public void update(URI uri) throws IOException{
		unpack();
		if(uri.equals(ispriteURI)){
			createi();
		}else if(uri.equals(hspriteURI)){
			createh();
		}else if(uri.equals(vspriteURI)){
			createv();
		}else if(uri.equals(tspriteURI)){
			createt();
		}
	}
	
	private URI getPath(URI from, String name){
		return QrONEUtils.relativize(from,basedir.resolve(name));
	}
	
	public void create() throws IOException {
		unpack();
		createi();
		createh();
		createv();
		createt();
		pack();
	}

	public void createi() throws IOException {
		int currentY = 0;
		int currentX = 0;
		if(pack.isprites.size() > 0 && pack.ilastsize != pack.isprites.size()){
			pack.ilastsize = pack.isprites.size();
			
			ImageBuffer iimage = service.createImage(pack.iWidth, pack.iHeight);
			currentY = 0;
			for (Iterator<ImagePart> i = pack.isprites.iterator(); i
					.hasNext();) {
				ImagePart part = i.next();
				iimage.drawImage(getImage(part.file), 
						new ImageRect(currentX, currentY, part.w, part.h), 
						new ImageRect(part.x, part.y, part.w, part.h));
				currentY += part.h;
			}
			iimage.writeTo(resolver.getOutputStream(ispriteURI));
		}
	}
	
	public void createv() throws IOException {
		int currentY = 0;
		int currentX = 0;
		if(pack.vsprites.size() > 0 && pack.vlastsize != pack.vsprites.size()){
			pack.vlastsize = pack.vsprites.size();
			
			ImageBuffer vimage = service.createImage(pack.vWidth, pack.vHeight);
			currentX = 0;
			for (Iterator<ImagePart> i = pack.vsprites.iterator(); i
					.hasNext();) {
				ImagePart part = i.next();
	
				currentY = 0;
				while (currentY < pack.vWidth) {
					if(currentY + part.h > pack.vWidth){
						vimage.drawImage(getImage(part.file), 
								new ImageRect(currentX, currentY, 
								part.w, 
								part.h - (currentY + part.h - pack.vWidth)), 
								new ImageRect(part.x, part.y, 
								part.w, 
								part.h - (currentY + part.h - pack.vWidth)));
					}else{
						vimage.drawImage(getImage(part.file), 
								new ImageRect(currentX, currentY, 
								part.w, 
								part.h), 
								new ImageRect(part.x, part.y, 
								part.w, 
								part.h));
					}
					currentY += part.h;
				}
	
				currentX += part.w;
			}
			vimage.writeTo(resolver.getOutputStream(vspriteURI));
		}
	}
	
	public void createh() throws IOException {
		int currentY = 0;
		int currentX = 0;
		
		if(pack.hsprites.size() > 0 && pack.hlastsize != pack.hsprites.size()){
			pack.hlastsize = pack.hsprites.size();
			
			ImageBuffer himage = service.createImage(pack.hWidth, pack.hHeight);
			currentY = 0;
			for (Iterator<ImagePart> i = pack.hsprites.iterator(); i
					.hasNext();) {
				ImagePart part = i.next();
	
				currentX = 0;
				while (currentX < pack.hHeight) {
					if(currentX + part.w > pack.hHeight){
						himage.drawImage(getImage(part.file), 
								new ImageRect(currentX, currentY, 
								part.w - (currentX + part.w - pack.hHeight), 
								part.h), 
								new ImageRect(part.x, part.y, 
								part.w - (currentX + part.w - pack.hHeight), 
								part.h));
					}else{
	
						himage.drawImage(getImage(part.file), 
								new ImageRect(currentX, currentY, 
								part.w, 
								part.h), 
								new ImageRect(part.x, part.y, 
								part.w, 
								part.h));
					}
					currentX += part.w;
				}
	
				currentY += part.h;
			}
			himage.writeTo(resolver.getOutputStream(hspriteURI));
		}
	}
	
	public void createt() throws IOException {
		if(useTransparentDot && !outTransparentDot){
			outTransparentDot = true;
			InputStream in = QrONEUtils.getResourceAsStream("1dot.png");
			OutputStream out = resolver.getOutputStream(tspriteURI);
			try{
				int buf;
			    while ((buf = in.read()) >= 0)
			        out.write(buf);
			}finally{
				in.close();
				out.close();
			}
		}
	}
	
	public String addISprite(URI file) throws IOException{
		ImageBuffer b = getImage(file);
		ImagePart part = new ImagePart(file, 0, 0, b.getWidth(), b.getHeight());
		/*
		if(iresults.containsKey(part)){
			return iresults.get(part);
		}
		
		if(iWidth < b.getWidth()){
			iWidth = b.getWidth();
		}
		iHeight += b.getHeight();
		
		String res ="width:" + b.getWidth() + "px;" 
			 + "height:" + b.getHeight() + "px;" 
			 + "background: no-repeat 0px -" + (iHeight-b.getHeight()) + "px url(" + getPath(file, isprite) + ");";
		iresults.put(part, res);
		*/
		return addISprite(part);
	}

	public String addISprite(ImagePart file) throws IOException{
		if(pack.iresults.containsKey(file)){
			return pack.iresults.get(file);
		}
		
		pack.isprites.add(file);
		if(pack.iWidth < file.w){
			pack.iWidth = file.w;
		}
		pack.iHeight += file.h;
		
		String res = "width:" + file.w + "px;" 
			 + "height:" + file.h + "px;" 
			 + "background: no-repeat 0px -" 
			 	+ (pack.iHeight-file.h) + "px url(" + getPath(file.file, isprite) + ");";
		pack.iresults.put(file, res);
		return res;
	}
	public String addVSprite(ImagePart file) throws IOException{
		if(pack.vresults.containsKey(file)){
			return pack.vresults.get(file);
		}
		
		pack.vsprites.add(file);
		if(pack.vHeight < file.h){
			pack.vHeight = file.h;
		}
		pack.vWidth += file.w;
		
		String res = "width:" + file.w + "px;" 
			 + "background: repeat-y -" + (pack.vWidth-file.w) + "px 0px url(" + getPath(file.file, vsprite) + ");";
		pack.vresults.put(file, res);
		return res;
	}

	public String addHSprite(ImagePart file) throws IOException{
		if(pack.hresults.containsKey(file)){
			return pack.hresults.get(file);
		}
		
		pack.hsprites.add(file);
		if(pack.hWidth < file.w){
			pack.hWidth = file.w;
		}
		pack.hHeight += file.h;
		
		String res = "height:" + file.h + "px;" 
			 + "background: repeat-x 0px -" + (pack.hHeight-file.h) + "px url(" + getPath(file.file, hsprite) + ");";
		pack.hresults.put(file, res);
		return res;
	}
	
	public URI addTransparentDot(){
		useTransparentDot = true;
		return basedir.resolve(tsprite);
	}
	
}

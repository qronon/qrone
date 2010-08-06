package org.qrone.r7.parser;

import java.io.Serializable;
import java.net.URI;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.qrone.img.ImageBuffer;
import org.qrone.img.ImageBufferService;
import org.qrone.r7.resolver.URIResolver;


public class ImagePack implements Serializable{
	private static final long serialVersionUID = -4273808957427639232L;
	
	public List<ImagePart> isprites = new CopyOnWriteArrayList<ImagePart>();
	public Map<ImagePart, String> iresults = new Hashtable<ImagePart, String>();
	public int iWidth;
	public int iHeight;
	public int ilastsize;
	
	public List<ImagePart> vsprites = new CopyOnWriteArrayList<ImagePart>();
	public Map<ImagePart, String> vresults = new Hashtable<ImagePart, String>();
	public int vWidth;
	public int vHeight;
	public int vlastsize;
	
	public List<ImagePart> hsprites = new CopyOnWriteArrayList<ImagePart>();
	public Map<ImagePart, String> hresults = new Hashtable<ImagePart, String>();
	public int hWidth;
	public int hHeight;
	public int hlastsize;
}

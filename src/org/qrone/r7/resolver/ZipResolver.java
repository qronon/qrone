package org.qrone.r7.resolver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ZipResolver extends AbstractURIResolver{
	private Map<String, ZipFile> map = new Hashtable<String, ZipFile>();
	private Set<ZipFile> set = new HashSet<ZipFile>();
	
	public ZipResolver(File file) {
		addFile(file);
	}
	
	private void addFile(File file){
		if(file.exists()){
			if(file.isDirectory()){
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					addFile(files[i]);
				}
			}else if(file.getName().endsWith(".zip")){
				try {
					ZipFile zf = new ZipFile(file);
					set.add(zf);
				} catch (ZipException e) {
				} catch (IOException e) {
				}
			}
		}
	}
	
	private ZipFile get(String path){
		if(map.containsKey(path)){
			return map.get(path);
		}else{
			for(ZipFile zf : set) {
				ZipEntry z = zf.getEntry(path);
				if(z != null){
					map.put(path, zf);
					return zf;
				}
			}
		}
		return null;
	}
	
	@Override
	public boolean exist(String path) {
		return get(path) != null;
	}
	
	@Override
	public boolean existPath(String path) {
		return false;
	}

	@Override
	public InputStream getInputStream(URI uri) throws IOException {
		String path = uri.toString();
		ZipFile zf = get(path);
		return zf.getInputStream(zf.getEntry(path));
	}

	@Override
	public OutputStream getOutputStream(URI uri) throws IOException {
		return null;
	}

	@Override
	public boolean remove(URI uri) {
		return false;
	}
	
}

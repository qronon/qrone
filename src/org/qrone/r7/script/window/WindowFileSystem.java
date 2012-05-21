package org.qrone.r7.script.window;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.mozilla.javascript.Scriptable;
import org.qrone.r7.resolver.URIFileSystem;
import org.qrone.r7.script.browser.Window;
import org.qrone.util.Stream;

public class WindowFileSystem implements WindowPrototype {
	private Window win;
	private URIFileSystem resolver;
	
	public WindowFileSystem( Window win ){
		this.win = win;
		resolver = win.getPortingService().getFileSystemService();
	}

	@Override
	public void init(Scriptable scr) {
		
	}
	
	public List<String> source_listFile(){
		return new ArrayList<String>(resolver.list());
	}
	
	public boolean source_existFile(String path){
		return resolver.exist(path);
	}
	
	public byte[] source_readFile(String path){
		if(resolver.exist(path)){
			try {
				return Stream.read(resolver.getInputStream(new URI(path)));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	
	public void source_writeFile(String path, byte[] data){
		try {
			OutputStream out = resolver.getOutputStream(new URI(path));
			out.write(data);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}

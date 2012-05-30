package org.qrone.r7.resolver;

import java.util.List;
import java.util.SortedSet;

public interface URIFileSystem extends URIResolver{
	public List<String> list();
	public void drop();
	
	public void write(String path, String data);
	public void writeBytes(String path, byte[] data);
	public byte[] readBytes(String path);
	public String read(String path);
}

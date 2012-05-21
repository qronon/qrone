package org.qrone.r7.resolver;

import java.util.SortedSet;

public interface URIFileSystem extends URIResolver{
	public SortedSet<String> list();
	public void drop();
}

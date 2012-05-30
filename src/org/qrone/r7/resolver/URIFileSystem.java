package org.qrone.r7.resolver;

import java.util.List;
import java.util.SortedSet;

public interface URIFileSystem extends URIResolver{
	public List<String> list();
	public void drop();
}

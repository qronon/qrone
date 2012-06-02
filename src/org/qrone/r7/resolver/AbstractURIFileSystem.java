package org.qrone.r7.resolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractURIFileSystem extends AbstractURIResolver implements URIFileSystem{
	
	@Override
	public List<String> list(String path) {
		List<String> list = list();
		Set<String> set = new HashSet<String>();
		
		for (String uri : list) {
			if(uri.startsWith(path)){
				String u = uri.substring(path.length());
				if(u.contains("/")){
					set.add(u.substring(0,u.indexOf('/')));
				}else{
					set.add(u);
				}
			}
		}
		
		return new ArrayList<String>(set);
	}
	
	
}

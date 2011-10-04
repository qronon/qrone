package org.qrone.r7;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.Scriptable;

public interface RepositoryService {
	public String add(Scriptable repo);
	public void remove(String id);
	public List<Map<String, Object>> list();
	public void reset(String id);
	
	public Map<String, InputStream> getFiles(URI uri);
}

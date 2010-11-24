package org.qrone.r7;

import java.util.List;
import java.util.Map;

public interface RepositoryService {
	public String add(String path, Map<String, String> repo);
	public void remove(String id);
	public List<Map<String, Object>> list();
}

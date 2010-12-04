package org.qrone.r7;

import java.util.List;
import java.util.Map;

import org.mozilla.javascript.Scriptable;

public interface RepositoryService {
	public String add(Scriptable repo);
	public void remove(String id);
	public List<Map<String, Object>> list();
}

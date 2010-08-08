package org.qrone.r7.script.browser;

import java.util.Map;

public interface LoginService {
	public String loginURL(String doneURL);
	public String loginURL(String url, Map<String, String> attrMap, String doneURL);
	public String logoutURL(String doneURL);
	public User getUser();
}

package org.qrone.login;

import java.util.Map;

import org.qrone.r7.script.browser.User;

public interface LoginService {
	public String loginURL(String doneURL);
	public String loginURL(String url, Map<String, String> attrMap, String doneURL);
	public String logoutURL(String doneURL);
	public User getUser();
}

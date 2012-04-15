package org.qrone.login;

import java.util.Map;

public interface LoginService {
	public String getOpenIDLoginURL(String url, Map<String, String> attrMap, String doneURL);
	
	public String getLoginURL(String doneURL);
	public String getLogoutURL(String doneURL);
	
}

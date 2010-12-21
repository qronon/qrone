package org.qrone.login;

import javax.servlet.http.HttpServletRequest;

public interface SecurityService {
	public String getTicket(HttpServletRequest request);
	public boolean validateTicket(HttpServletRequest request, String ticket);
}

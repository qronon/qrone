package org.qrone.login;

import javax.servlet.http.HttpServletRequest;

public interface SecurityService {
	public String getTicket();
	public boolean isSecured(HttpServletRequest request);
}

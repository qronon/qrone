package org.qrone.r7;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.r7.app.QrONEURIHandler;
import org.qrone.r7.handler.URIHandler;

public class PortingServlet extends HttpServlet {
	private PortingService s;
	private URIHandler h;

	public void setPortingService(PortingService portingService) {
		s = portingService;
	}

	public PortingServlet(){
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(h == null)
			h = new QrONEURIHandler(getServletContext(), s);
		h.handle(request, response, request.getPathInfo(), "", "");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(h == null)
			h = new QrONEURIHandler(getServletContext(), s);
		doGet(request, response);
	}

}

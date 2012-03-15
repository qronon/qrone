package org.qrone.r7;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qrone.r7.handler.URIHandler;

public class PortingServlet extends HttpServlet {
	private URIHandler h;

	public void setPortingService(PortingService portingService) {
		h = new PortingURIHandler(getServletContext(), portingService);
	}

	public PortingServlet(){
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		h.handle(request, response, request.getPathInfo(), "", "");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

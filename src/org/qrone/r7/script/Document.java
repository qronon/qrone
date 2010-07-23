package org.qrone.r7.script;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Document {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private Writer writer;
	public Document(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		try {
			this.writer = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void write(String out){
		try {
			writer.append(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

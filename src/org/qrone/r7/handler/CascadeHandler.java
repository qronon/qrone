package org.qrone.r7.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CascadeHandler implements URIHandler{
	private List<URIHandler> list = new ArrayList<URIHandler>();

	public void add(URIHandler r){
		list.add(r);
	}

	@Override
	public boolean handle(HttpServletRequest request, HttpServletResponse response) {
		for (Iterator<URIHandler> i = list.iterator(); i.hasNext();) {
			URIHandler r = i.next();
			if(r.handle(request,response)){
				return true;
			}
		}
		return false;
	}
}

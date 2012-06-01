package org.qrone.r7.script;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.qrone.r7.script.browser.User;
import org.qrone.util.QrONEUtils;
import org.qrone.util.QueryString;
import org.qrone.util.Stream;

public class ServletScope{
	public HttpServletRequest request;
	public HttpServletResponse response;
	public URI uri;
	public String path;
	public String leftpath;

	public byte[] body;
	public String text;
	public Map<String, Object> get;
	public Map<String, Object> post;
	public List<FileItem> fileItemList;
	public boolean secure;
	
	private User user;
	
	public ServletScope(HttpServletRequest request, HttpServletResponse response, URI uri, String path, String leftpath) {
		this.request = request;
		this.response = response;
		this.uri = uri;
		this.path = path;
		this.leftpath = leftpath;
		
		get = parseQueryString(request.getQueryString());
		
		user = (User)request.getAttribute("User");
		secure = user.validateTicket(getParameter(".ticket"));
		if(!isMultipart()){
			parseForm();
		}else{
			parseMultipart();
		}
		
	}
	
	public void close(){
		if(fileItemList != null){
			for (FileItem fileItem : fileItemList) {
				fileItem.delete();
			}
		}
	}
	
	private boolean isMultipart(){
		String ct = request.getHeader("Content-Type");
		return ct != null && ct.equals("multipart/form-data");
	}
	
	private void parseForm(){
		try {
			InputStream in = request.getInputStream();
			byte[] b = Stream.read(in);
			String t = QrONEUtils.getString(b, request.getHeader("Content-Type"));
			
			Map<String, Object> p = parseQueryString(text);

			Object pt = p.get(".ticket");
			if( secure || user.validateTicket((String)pt) ){
				post = p;
				body = b;
				text = t;
			}

		} catch (IOException e) {}
	}
	
	private void parseMultipart(){
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1024);
		
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(-1);
		
		try {
			fileItemList = upload.parseRequest(request);
			Map<String, Object> p = new HashMap<String, Object>();
			for (FileItem fileItem : fileItemList) {
				if(fileItem.isFormField()){
					Object o = p.get(fileItem.getFieldName());
					if(o == null){
						p.put(fileItem.getFieldName(), fileItem.getString());
					}else if(o instanceof String){
						List<String> l = new ArrayList<String>();
						l.add((String)o);
						l.add(fileItem.getString());
						p.put(fileItem.getFieldName(), l);
					}else{
						List<String> l = (List<String>)o;
						l.add(fileItem.getString());
					}
				}else{
					String fileName = fileItem.getName();
					if ((fileName != null) && (!fileName.equals(""))) {
						fileName = (new File(fileName)).getName();
					}
				}
			}
			
			Object pt = p.get(".ticket");
			if( secure || user.validateTicket((String)pt) ){
				post = p;
			}
			
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
	}
	
	public String getParameter(String name){
		Object param = get.get(name);
		if(param instanceof String){
			return (String)param;
		}else{
			List<String> list = (List<String>)param;
			if(list != null && list.size() > 0){
				return list.get(0);
			}
		}
		return null;
	}

	private Map<String, Object>  parseQueryString(String query){
		QueryString qs = new QueryString(query);
		return qs.getParameterMapSingle();
	}
}
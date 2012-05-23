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
	private Map<String, Object> post;
	private List<FileItem> fileItemList;
	
	public ServletScope(HttpServletRequest request, HttpServletResponse response, URI uri, String path, String leftpath) {
		this.request = request;
		this.response = response;
		this.uri = uri;
		this.path = path;
		this.leftpath = leftpath;
		
		get = parseQueryString(request.getQueryString());
		
		
	}
	
	public void close(){
		if(fileItemList != null){
			for (FileItem fileItem : fileItemList) {
				fileItem.delete();
			}
		}
	}
	
	public Map<String, Object> getPost(User user, boolean secure){
		if(post == null){
			
			if(request.getHeader("Content-Type").equals("multipart/form-data")){
				
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
					
					if(validateTicket(user, secure, p)){
						post = p;
						return post;
					}else{
						return null;
					}
					
				} catch (FileUploadException e) {
					e.printStackTrace();
				}
				
			}else{
				try {
					InputStream in = request.getInputStream();
					body = Stream.read(in);
					text = QrONEUtils.getString(body, request.getHeader("Content-Type"));
					
					Map<String, Object> p = parseQueryString(text);
					if(validateTicket(user, secure, p)){
						post = p;
						return post;
					}else{
						return null;
					}

				} catch (IOException e) {}
			}
		}
		return null;
	}
	
	private boolean validateTicket(User user, boolean secure, Map<String, Object> p){
		Object pt = p.get(".ticket");
		if( secure
				|| pt != null && pt instanceof String && user.validateTicket((String)pt) ){
			return true;
		}
		return false;
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
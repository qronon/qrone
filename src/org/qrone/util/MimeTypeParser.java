package org.qrone.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MimeTypeParser {
	private Map<String, String> map;
	public MimeTypeParser(){
		
	}
	
	public void parse(InputStream in) throws IOException{
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		map = new HashMap<String, String>();
		
		String line;
		while((line = r.readLine()) != null){
			int cm = line.indexOf('#');
			if(cm >= 0){
				line = line.substring(cm);
			}
			
			line.trim();
			String[] sline = line.split("\\s+");
			if(sline.length > 1){
				for (int i = 1; i < sline.length; i++) {
					if(sline[i].length() > 0)
						map.put(sline[i], sline[0]);
				}
				
			}
		}
	}
	
	public String getMimeType(String ext){
		return map.get(ext);
	}
}

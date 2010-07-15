package org.qrone.r7.parser;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HTML5Template implements HTML5Writer{
	private List<CharSequence> list = new ArrayList<CharSequence>();
	private Map<String, StringBuffer> map = new Hashtable<String, StringBuffer>();
	private Map<StringBuffer, String> rmap = new Hashtable<StringBuffer, String>();

	public void append(String key, String value){
		if(value != null){
			StringBuffer b = map.get(key);
			if(b == null){
				b = new StringBuffer();
				map.put(key, b);
				rmap.put(b, key);
				list.add(b);
			}else{
				list.add(b);
			}
			b.append(value);
		}
	}
	
	public void setValue(String key, String value){
		StringBuffer b = map.get(key);
		if(b == null){
			b = new StringBuffer();
			map.put(key, b);
			rmap.put(b, key);
		}else{
			b.delete(0, b.length());
		}
		b.append(value);
	}

	public void append(char str){
		list.add(String.valueOf(str));
	}

	public void append(CharSequence str){
		list.add(str);
	}
	
	public void append(String str){
		list.add(str);
	}
	
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		for (Iterator<CharSequence> i = list.iterator(); i
				.hasNext();) {
			b.append(i.next());
		}
		return b.toString();
	}
}

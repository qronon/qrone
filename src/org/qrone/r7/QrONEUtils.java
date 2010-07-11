package org.qrone.r7;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class QrONEUtils{

	public static InputStream getResourceAsStream(String name) throws IOException {
		InputStream in = QrONEUtils.class.getResourceAsStream("resource/" + name);
		if(in != null){
			return in;
		}
		
		in = new FileInputStream("src/org/qrone/r7/resource/" + name);
		return in;
	}
	
	public static String getContent(File file, String x) throws IOException{
		File s = new File(file, x);
		if(s.exists()){
			return QrONEUtils.convertStreamToString(new FileInputStream(s));
		}
		return null;
	}
	
	public static String getResource(String name) throws IOException {
		InputStream in = QrONEUtils.class.getResourceAsStream("resource/" + name);
		if(in != null){
			return convertStreamToString(in);
		}
		
		in = new FileInputStream("src/org/qrone/r7/resource/" + name);
		return convertStreamToString(in);
	}
	
	public static String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                is.close();
            }
            return sb.toString();
        } else {        
            return "";
        }
    }

	public static String escape(String str){
		StringBuffer b = new StringBuffer();
		char[] ch = str.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			switch (ch[i]) {
			case '\t':
			case '\r':
			case '\n':
			case ' ':
				b.append(' ');
				break;
			case '<':
				b.append("&lt;");
				break;
			case '>':
				b.append("&gt;");
				break;
			case '"':
				b.append("&quot;");
				break;
			case '&':
				b.append("&amp;");
				break;
			case '\u00A0':
				b.append("&nbsp;");
				break;
			case '\0':
				break;
			default:
				b.append(ch[i]);
				break;
			}
		}
		return b.toString();
	}
}

package org.qrone.r7;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

public class QrONEUtils{

    public static void main(String[] args) {
    	String[] a = {"-v", "-n", "site/test"};
    	/*
    	try{
    		URI from = new URI("from/file.html");
    		URI to = new URI("to/file.png");
    		System.out.println(relativize(from.toString(), to.toString()));
    		from.relativize(from)
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	*/
    	QrONECompressor.main(a);
    	QrONECompressor.main(a);
	}

    public static URI relativize(URI basePath, URI targetPathString) {
    	String uri = relativize(basePath.toString(), targetPathString.toString());
    	try {
			return new URI(uri);
		} catch (URISyntaxException e) {
			return targetPathString;
		}
    }
    
    public static String relativize(String basePath, String targetPathString) {
        // We modify targetPath to become the result.
		StringBuilder targetPath = new StringBuilder(targetPathString);
	
		// Find the longest common initial sequence of path elements.
		int length = Math.min(basePath.length(), targetPath.length());
		int diff = 0;
		for (int i = 0; i < length; i++) {
		    char c = basePath.charAt(i);
		    if (c != targetPath.charAt(i))
			break;
		    if (c == '/')
			diff = i + 1;
		}
	
		// Remove the common initial elements from the target, including
		// their trailing slashes.
		targetPath.delete(0, diff);
	
		// Count remaining complete path elements in the base,
		// prefixing the target with "../" for each one.
		for (int slash = basePath.indexOf('/', diff); slash > -1;
		     slash = basePath.indexOf('/', slash + 1))
		    targetPath.insert(0, "../");
	
		// Make sure the result is not empty.
		if (targetPath.length() == 0)
		    targetPath.append("./");

        return targetPath.toString();
    }
    
	private static int uniquekey = 0;
	public static String uniqueid(){
		return "qid" + (++uniquekey);
	}

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

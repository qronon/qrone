package org.qrone.r7.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.WeakHashMap;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

public class JSParser {
	public static String clean(String js){
		return js
			.replaceAll("^<!\\[CDATA\\[", "")
			.replaceAll("\\]\\]>$", "")
			.replaceAll("^<!--", "")
			.replaceAll("-->$", "");
	}
	
	public static void compress(Reader r, Writer w, boolean qroneSymbol) 
			throws EvaluatorException, IOException{
		JavaScriptCompressor jsc = new JavaScriptCompressor(r, new ErrorReporter() {
			@Override
			public void warning(String arg0, String arg1, int arg2, String arg3,
					int arg4) {
			}
			
			@Override
			public EvaluatorException runtimeError(String arg0, String arg1, int arg2,
					String arg3, int arg4) {
				return new EvaluatorException(arg0, arg1, arg2, arg3, arg4);
			}
			
			@Override
			public void error(String arg0, String arg1, int arg2, String arg3, int arg4) {
				
			}
		});
		jsc.compress(w, -1, true, false, false, false, qroneSymbol);
	}
	
	public static String compress(Reader r, boolean qroneSymbol) 
			throws EvaluatorException, IOException{
		StringWriter w = new StringWriter();
		compress(r, w, qroneSymbol);
		return w.toString();
	}
	
	public static void compress(String r, Writer w, boolean qroneSymbol) 
			throws EvaluatorException, IOException{
		compress(new StringReader(r), w, qroneSymbol);
	}

	public static String compress(String r){
		return compress(r, null);
	}

	public static String compress(String r, String replace){
		try {
			StringWriter w = new StringWriter();
			compress(new StringReader(r), w, replace != null);
			String wstr = w.toString();
			if(replace != null){
				wstr = wstr.replace("__QRONE_PREFIX_NAME__",replace);
			}
			return wstr;
		} catch (EvaluatorException e) {
		} catch (IOException e) {
		}
		return r;
	}
		
}

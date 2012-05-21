package org.qrone.r7.parser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public class HTML5StreamWriter implements HTML5Writer{
	private Writer w;
	public HTML5StreamWriter(Writer w){
		this.w = w;
	}

	@Override
	public void append(char c) {
		try {
			w.append(c);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void append(String str) {
		try {
			w.append(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void append(String key, String value) {

		try {
			w.append(value);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

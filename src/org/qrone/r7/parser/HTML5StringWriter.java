package org.qrone.r7.parser;

public class HTML5StringWriter implements HTML5Writer{
	private StringBuilder b = new StringBuilder();

	@Override
	public void append(char c) {
		b.append(c);
	}

	@Override
	public void append(String str) {
		b.append(str);
	}

	@Override
	public void append(String key, String value) {
		b.append(value);
	}

	@Override
	public String toString() {
		return b.toString();
	}
	
}

package org.qrone.util;

/**
 Original pseudocode   : Thomas Weidenfeller
 Implementation tweaked: Aki Nieminen

 http://www.unicode.org/unicode/faq/utf_bom.html
 BOMs:
 00 00 FE FF    = UTF-32, big-endian
 FF FE 00 00    = UTF-32, little-endian
 FE FF          = UTF-16, big-endian
 FF FE          = UTF-16, little-endian
 EF BB BF       = UTF-8

 Win2k Notepad:
 Unicode format = UTF-16LE
 ***/

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This inputstream will recognize unicode BOM marks and will skip bytes if
 * getEncoding() method is called before any of the read(...) methods.
 * 
 * Usage pattern: String enc = "ISO-8859-1"; // or NULL to use systemdefault
 * FileInputStream fis = new FileInputStream(file); UnicodeInputStream uin = new
 * UnicodeInputStream(fis, enc); enc = uin.getEncoding(); // check for BOM mark
 * and skip bytes InputStreamReader in; if (enc == null) in = new
 * InputStreamReader(uin); else in = new InputStreamReader(uin, enc);
 */
public class Tab2WhiteInputStream extends FilterInputStream {
	public Tab2WhiteInputStream(InputStream in) {
		super(in);
	}

	@Override
	public int read() throws IOException {
		int c = in.read();
		return c == '\t' ? ' ' : c;
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int c = super.read(b, off, len);
		for (int i = off; i < off + len; i++) {
			if(b[i] == '\t') b[i] = ' ';
		}
		return c;
	}
}
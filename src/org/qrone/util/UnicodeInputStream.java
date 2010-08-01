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

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

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
public class UnicodeInputStream extends PushbackInputStream {
	boolean isInited = false;
	String defaultEnc;
	String encoding;

	private static final int BOM_SIZE = 4;

	public UnicodeInputStream(InputStream in) {
		this(in, "utf8");
	}
	
	public UnicodeInputStream(InputStream in, String encoding) {
		super(in,BOM_SIZE);
		this.defaultEnc = encoding;
		this.encoding = encoding;
	}

	public String getEncoding() {
		return encoding;
	}

	/**
	 * Read-ahead four bytes and check for BOM marks. Extra bytes are unread
	 * back to the stream, only BOM bytes are skipped.
	 */
	protected void init() throws IOException {
		if (isInited)
			return;

		byte bom[] = new byte[BOM_SIZE];
		int n, unread;
		n = super.read(bom, 0, bom.length);

		if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB)
				&& (bom[2] == (byte) 0xBF)) {
			encoding = "UTF-8";
			unread = n - 3;
		} else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
			encoding = "UTF-16BE";
			unread = n - 2;
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
			encoding = "UTF-16LE";
			unread = n - 2;
		} else if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00)
				&& (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF)) {
			encoding = "UTF-32BE";
			unread = n - 4;
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)
				&& (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00)) {
			encoding = "UTF-32LE";
			unread = n - 4;
		} else {
			// Unicode BOM mark not found, unread all bytes
			encoding = defaultEnc;
			unread = n;
		}

		if (unread > 0)
			super.unread(bom, (n - unread), unread);

		isInited = true;
	}

	@Override
	public void close() throws IOException {
		isInited = true;
		super.close();
	}

	@Override
	public int read() throws IOException {
		if(!isInited) init();
		return super.read();
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		if(!isInited) init();
		return super.read(b);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if(!isInited) init();
		return super.read(b, off, len);
	}
}
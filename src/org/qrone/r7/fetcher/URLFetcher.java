package org.qrone.r7.fetcher;

import java.io.IOException;
import java.io.InputStream;

public interface URLFetcher {
	public InputStream fetch(String url) throws IOException;
	public InputStream fetch(String url, byte[] body) throws IOException;
}

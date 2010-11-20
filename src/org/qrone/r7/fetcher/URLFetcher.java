package org.qrone.r7.fetcher;

import java.io.InputStream;

public interface URLFetcher {
	public InputStream fetch(String url);
}

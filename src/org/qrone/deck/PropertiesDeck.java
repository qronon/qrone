package org.qrone.deck;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.qrone.r7.resolver.URIResolver;

public class PropertiesDeck extends XDeck<Map<Object, Object>>{

	public PropertiesDeck(URIResolver resolver) {
		super(resolver);
	}

	@Override
	protected Map<Object, Object> compile(URI uri, InputStream in, String encoding)
			throws Exception {
		Properties p = new Properties();
		p.load(in);
		return p;
	}

}

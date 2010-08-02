package org.qrone.deck;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

import org.qrone.r7.resolver.URIResolver;
import org.qrone.util.XDeck;

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

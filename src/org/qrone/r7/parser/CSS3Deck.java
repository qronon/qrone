package org.qrone.r7.parser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import org.qrone.r7.resolver.URIResolver;
import org.qrone.util.XDeck;
import org.w3c.css.sac.InputSource;

public class CSS3Deck extends XDeck<CSS3OM>{
    public CSS3Deck(URIResolver resolver){
    	super(resolver);
    }

	@Override
	public CSS3OM compile(URI uri, InputStream in, String encoding)
			throws Exception {
		CSS3OM s = new CSS3OM();
		s.parse(uri, new InputSource(new InputStreamReader(in, encoding)));
		return s;
	}

	public CSS3OM compile(URI uri, String source)
			throws Exception {
		CSS3OM s = new CSS3OM();
		s.parse(uri, source);
		return s;
	}
    
}

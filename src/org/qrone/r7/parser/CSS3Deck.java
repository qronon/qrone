package org.qrone.r7.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.js2j.SugarWrapFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.qrone.deck.XDeck;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.script.Window;
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

package org.qrone.r7.format;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.ho.yaml.Yaml;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.util.Tab2WhiteInputStream;
import org.qrone.util.XDeck;

public class YAML extends XFormat<Object>{

	public YAML(URIResolver resolver) {
		super(resolver);
	}

	@Override
	public Object compile(URI uri, InputStream in, String encoding) throws IOException {
		return Yaml.load(new Tab2WhiteInputStream(in));
	}

	@Override
	public Object decode(String data) {
		return Yaml.load(data.replaceAll("\\t", " "));
	}

	@Override
	public String encode(Object data) {
		throw new UnsupportedOperationException();
	}
	
	

}

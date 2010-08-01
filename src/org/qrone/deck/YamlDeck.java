package org.qrone.deck;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;

import org.ho.yaml.Yaml;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.util.Tab2WhiteInputStream;

public class YamlDeck extends XDeck<Object>{

	public YamlDeck(URIResolver resolver) {
		super(resolver);
	}

	@Override
	public Object compile(URI uri, InputStream in, String encoding) throws IOException {
		return Yaml.load(new Tab2WhiteInputStream(in));
	}

	public Object compile(URI uri, String str) throws IOException {
		return Yaml.load(str);
	}

}

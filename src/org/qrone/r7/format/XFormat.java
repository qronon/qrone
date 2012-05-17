package org.qrone.r7.format;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.qrone.r7.resolver.URIResolver;
import org.qrone.util.QrONEUtils;
import org.qrone.util.Stream;
import org.qrone.util.XDeck;

public abstract class XFormat<T> extends XDeck<T>{
	public XFormat(URIResolver resolver) {
		super(resolver);
	}


	@Override
	protected T compile(URI uri, InputStream in, String encoding)
			throws Exception {
		return decode(new String(Stream.read(in)));
	}
	
	public T parse(String data){
		return decode(data);
	}
	
	public T load(String uri) throws IOException, URISyntaxException{
		byte[] data = Stream.read(resolver.getInputStream(new URI(uri)));
		return decode(new String(data));
	}
	
	public abstract T decode(String data);
	public abstract String encode(T data);
}

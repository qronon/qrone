package org.qrone.r7.script.window;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.URLCodec;
import org.mozilla.javascript.Scriptable;
import org.qrone.r7.PortingService;
import org.qrone.r7.format.JSON;
import org.qrone.r7.format.JavaProperties;
import org.qrone.r7.format.Textile;
import org.qrone.r7.format.YAML;
import org.qrone.r7.resolver.URIResolver;
import org.qrone.r7.script.browser.Window;
import org.qrone.util.Digest;

public class WindowFormats implements WindowPrototype {
	private Window win;
	private URIResolver resolver;
	
	public YAML YAML;
	public Textile Textile;
	public JavaProperties JavaProperties;

	public WindowFormats( Window win ){
		this.win = win;
		resolver = win.getPortingService().getURIResolver();
	}
	
	@Override
	public void init(Scriptable scr) {
		YAML = new YAML(resolver);
		Textile = new Textile(resolver);
		JavaProperties = new JavaProperties(resolver);
	}
	
	private JavaProperties propDeck;
	public Object load_properties(String path) throws IOException, URISyntaxException{
		if(propDeck == null)
			propDeck = new JavaProperties(resolver);
		return propDeck.compile(win.resolvePath(path));
	}

	private YAML yamlDeck;
	public Object load_yaml(String path) throws IOException, URISyntaxException{
		if(yamlDeck == null)
			yamlDeck = new YAML(resolver);
		return yamlDeck.compile(win.resolvePath(path));
	}
	
	private Textile textileDeck;
	public String load_textile(String path) throws IOException, URISyntaxException{
		if(textileDeck == null)
			textileDeck = new Textile(resolver);
		return textileDeck.compile(win.resolvePath(path));
	}

}

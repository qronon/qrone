package org.qrone.deck;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;
import org.qrone.r7.resolver.URIResolver;

public class TextileDeck extends XDeck<String>{
	private static MarkupParser parser;
	public TextileDeck(URIResolver resolver) {
		super(resolver);
	}

	public String compile(URI uri, InputStream in, String encoding) throws IOException{
		StringWriter writer = new StringWriter();
		getParser(writer).parse(new InputStreamReader(in, encoding));
		return writer.toString();
	}

	public String compile(URI uri, String textile){
		StringWriter writer = new StringWriter();
		getParser(writer).parse(textile);
		return writer.toString();
	}

	public static String parse(String textile) {
		StringWriter writer = new StringWriter();
		getParser(writer).parse(textile);
		return writer.toString();
	}
	
	public static MarkupParser getParser(Writer writer) {
		if(parser == null){
			HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
			builder.setEmitAsDocument(false);
	
			parser = new MarkupParser(new TextileLanguage());
			parser.setBuilder(builder);
		}
		return parser;
	}
}

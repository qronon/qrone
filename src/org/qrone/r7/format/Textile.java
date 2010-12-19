package org.qrone.r7.format;

import java.io.StringWriter;
import java.io.Writer;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;
import org.qrone.r7.resolver.URIResolver;

public class Textile extends XFormat<String>{
	private static MarkupParser parser;
	public Textile(URIResolver resolver) {
		super(resolver);
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

	@Override
	public String decode(String data) {
		StringWriter writer = new StringWriter();
		getParser(writer).parse(data);
		return writer.toString();
	}

	@Override
	public String encode(String data) {
		throw new UnsupportedOperationException();
	}
}

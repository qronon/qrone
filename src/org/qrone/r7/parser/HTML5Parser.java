package org.qrone.r7.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import nu.validator.htmlparser.common.XmlViolationPolicy;
import nu.validator.htmlparser.dom.HtmlDocumentBuilder;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class HTML5Parser {
	public static Document parse(InputSource source) throws SAXException, IOException{
		HtmlDocumentBuilder parser = new HtmlDocumentBuilder();
		source.setEncoding("utf8");
		parser.setXmlPolicy(XmlViolationPolicy.ALLOW);
		parser.setScriptingEnabled(true);
		return parser.parse(source);
	}

	public static Document parse(Reader r) throws SAXException, IOException{
		return parse(new InputSource(r));
	}

	public static Document parse(File f) throws SAXException, IOException{
		return parse(new InputSource(new FileReader(f)));
	}
	
	public static Document parse(String r) throws SAXException, IOException{
		return parse(new InputSource(r));
	}
}

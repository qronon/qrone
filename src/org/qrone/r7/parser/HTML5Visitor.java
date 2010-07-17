package org.qrone.r7.parser;

import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public abstract class HTML5Visitor {

	public void visit(Document e){
		accept(e);
	}
	
	public abstract void visit(Element e);
	public abstract void visit(Text n);

	public void visit(CDATASection n) {
		System.err.println("CDATASection found = " + n.getNodeValue());
	}

	public void visit(CharacterData n) {
		System.err.println("CharacterData found = " + n.getNodeValue());
	}

	public void visit(ProcessingInstruction n) {
		System.err.println("ProcessingInstruction found = " + n.getNodeValue());
	}

	public void visit(DocumentType n) {
		System.err.println("DocumentType found = " + n.getNodeValue());
	}
	
	
	protected void accept(Document e){
		visit(e.getDocumentElement());
	}

	protected void accept(Element e){
		NodeList l = e.getChildNodes();
		for (int i = 0; i < l.getLength(); i++) {
			dispatch(l.item(i));
		}
	}
	
	
	protected void dispatch(Node n){
		if(n instanceof Element)
			visit((Element)n);
		else if(n instanceof CDATASection)
			visit((CDATASection)n);
		else if(n instanceof Text)
			visit((Text)n);
		else if(n instanceof CharacterData)
			visit((CharacterData)n);
		else if(n instanceof ProcessingInstruction)
			visit((ProcessingInstruction)n);
		else if(n instanceof DocumentType)
			visit((DocumentType)n);
	}
}

package org.qrone.coder;

import org.qrone.coder.render.QLangBase;

public class QNumber extends QCodeBase {
	public Number number;
	public QNumber(Number number) {
		this.number = number;
	}

	public void visit(QLangBase base){
	}
}

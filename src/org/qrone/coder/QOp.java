package org.qrone.coder;

import org.qrone.coder.render.QLangBase;

public class QOp extends QCodeBase {
	public String op;
	public QOp(String op) {
		this.op = op;
	}

	public void visit(QLangBase base){
	}
}

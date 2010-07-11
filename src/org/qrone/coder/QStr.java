package org.qrone.coder;

import org.qrone.coder.render.QLangBase;

public class QStr extends QCodeBase {
	public StringBuilder str = new StringBuilder();
	public QStr(String str) {
		this.str.append(str);
	}

	public void visit(QLangBase base){
	}
}

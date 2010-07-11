package org.qrone.coder;

import org.qrone.coder.render.QLangBase;

public class QVar extends QCodeBase {
	public String type;
	public String name;
	public boolean ref = false;
	public QVar(String type, String name) {
		this.type = type;
		this.name = name;
	}

	public void visit(QLangBase base){
	}
}

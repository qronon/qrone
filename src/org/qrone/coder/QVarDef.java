package org.qrone.coder;

public class QVarDef extends QCodeBase {
	public QVar var;
	public QState state;

	public QVarDef(String type, String name) {
		var = new QVar(type, name);
	}
	
	public QState state(){
		this.state = new QState();
		return state;
	}

}

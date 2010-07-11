package org.qrone.coder;


public class QBlock extends QCodeBase {

	public QState state(){
		return add(new QState());
	}
	
	public QClause ifclause(QState cond){
		return add(new QClause("if"));
	}

	public QClause elseifclause(QState cond){
		return add(new QClause("else if"));
	}
	
	public QClause elseclause(){
		return add(new QClause("else"));
	}
}

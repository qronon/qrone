package org.qrone.coder;

import org.qrone.coder.render.QLangBase;

public class QChoice extends QCodeBase {
	public QState cond;
	public QState yes;
	public QState no;
	public QChoice(QState cond, QState yes, QState no) {
		this.cond = cond;
		this.yes = yes;
		this.no = no;
	}

	public void visit(QLangBase base){
	}
}

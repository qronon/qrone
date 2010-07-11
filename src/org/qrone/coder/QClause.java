package org.qrone.coder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QClause extends QBlock {
	public String type;
	public List<QState> args = new ArrayList<QState>();
	
	public QClause(String type, QState ... args) {
		this.type = type;
		this.args.addAll(Arrays.asList(args));
	}
}

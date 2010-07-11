package org.qrone.coder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QCall extends QCodeBase {
	public String name;
	public List<QState> args = new ArrayList<QState>();
	
	public QCall(String name, QState[] args) {
		this.name = name;
		this.args.addAll(Arrays.asList(args));
	}
	
	public QState state(){
		QState state = new QState();
		args.add(state);
		return state;
	}
}

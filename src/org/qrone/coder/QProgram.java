package org.qrone.coder;


public class QProgram extends QCodeBase {
	public QClass createClass(String name){
		return add(new QClass(name));
	}
}

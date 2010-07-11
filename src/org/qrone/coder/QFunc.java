package org.qrone.coder;

import java.util.ArrayList;
import java.util.List;

public class QFunc extends QBlock {
	public List<QVar> args = new ArrayList<QVar>();
	
	public QFunc arg(String type, String name){
		args.add(new QVar(type, name));
		return this;
	}
}

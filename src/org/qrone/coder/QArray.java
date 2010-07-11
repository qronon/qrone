package org.qrone.coder;

import java.util.ArrayList;
import java.util.List;

public class QArray extends QCodeBase {
	public List<QState> array = new ArrayList<QState>();
	
	public QArray push(QState item){
		array.add(item);
		return this;
	}
}

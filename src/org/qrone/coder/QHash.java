package org.qrone.coder;

import java.util.Hashtable;
import java.util.Map;

public class QHash extends QCodeBase {
	public Map<QState,QState> hash = new Hashtable<QState,QState>();
	
	public QHash put(QState key, QState value){
		hash.put(key, value);
		return this;
	}
}

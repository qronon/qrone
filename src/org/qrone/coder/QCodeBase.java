package org.qrone.coder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.qrone.coder.render.QLangBase;

public abstract class QCodeBase {
	public QCodeBase peek;
	public List<QCodeBase> codes = new ArrayList<QCodeBase>();
	
	public void visit(QLangBase base){
		for (Iterator<QCodeBase> i = codes.iterator(); i
				.hasNext();) {
			QCodeBase cls = i.next();
			base.accept(cls);
		}
	}
	
	public <T extends QCodeBase> T add(T code){
		codes.add(code);
		peek = code;
		return code;
	}
}

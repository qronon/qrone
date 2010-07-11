package org.qrone.coder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.qrone.coder.render.QLangBase;

public abstract class QCodeBase {
	public List<QCodeBase> codes = new ArrayList<QCodeBase>();
	
	public void visit(QLangBase base){
		for (Iterator<QCodeBase> i = codes.iterator(); i
				.hasNext();) {
			QCodeBase cls = i.next();
			base.accept(cls);
		}
	}
	
	public QCodeBase peek(){
		if(codes.isEmpty()) return null;
		return codes.get(codes.size()-1);
	}
	
	public <T extends QCodeBase> T add(T code){
		codes.add(code);
		return code;
	}
}

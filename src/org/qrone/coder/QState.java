package org.qrone.coder;

import java.util.Iterator;

import org.qrone.coder.render.QLangBase;

public class QState extends QCodeBase {
	public QState news(String name, QState... call){
		ops("new");
		add(new QCall(name, call));
		return this;
	}
	
	public QState returns(){
		ops("return");
		return this;
	}

	public QState member(){
		add(new QVar("this", "this"));
		return this;
	}
	
	public QState setTo(String type, String name){
		var(type,name);
		ops("=");
		return this;
	}

	public QState number(Number num){
		add(new QNumber(num));
		return this;
	}

	public QState property(String name){
		ops("[");
		str(name);
		ops("]");
		return this;
	}
	
	public QState hash(QHash hash){
		add(hash);
		return this;
	}

	public QState array(QArray array){
		add(array);
		return this;
	}
	
	public QState array(QState... items){
		QArray a = new QArray();
		for (int i = 0; i < items.length; i++) {
			a.push(items[i]);
		}
		add(a);
		return this;
	}
	
	public QState str(String str){
		if(peek instanceof QStr){
			((QStr)peek).str.append(str);
		}else{
			add(new QStr(str));
		}
		return this;
	}
	
	public QState var(String type, String name){
		if(peek instanceof QVar || peek instanceof QCall){
			ops("->");
		}
		
		if(name.matches("^[a-zA-Z][0-9a-zA-Z_]*$")){
			QVar var = new QVar(type, name);
			var.ref = true;
			add(var);
		}else{
			new IllegalArgumentException();
		}
		return this;
		
	}
	
	public QState choice(QState cond, QState yes, QState no){
		add(new QChoice(cond, yes, no));
		return this;
	}

	public QState call(String name, QState... call){
		if(peek instanceof QVar || peek instanceof QCall){
			ops("->");
		}
		add(new QCall(name, call));
		return this;
	}
	
	public QState ops(String op){
		add(new QOp(op));
		return this;
	}
	
	public void visit(QLangBase base){
		boolean op = true;
		for (Iterator<QCodeBase> iterator = codes.iterator(); iterator
				.hasNext();) {
			QCodeBase o = iterator.next();
			
			if(o instanceof QOp){
				op = true;
			}else{
				if(!op){
					 base.accept(new QOp("."));
				}
				op = false;
			}
			base.accept(o);
		}
	}
}

package org.qrone.coder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.qrone.coder.render.QLangBase;

public class QClass extends QCodeBase {
	public static final String STRING = "String";

	public String extend;
	public Set<QVarDef> variables = new HashSet<QVarDef>();
	public QFunc constructor;
	
	public String className;

	public QClass(String name){
		className = name;
	}
	
	public QVarDef variable(String type, String name){
		QVarDef def = new QVarDef(type, name);
		variables.add(def);
		return def;
	}
	
	public QFunc constructor(){
		constructor = new QFunc();
		return constructor;
	}
	
	public QMethod method(String name){
		return add(new QMethod(name));
	}
	
	public void removeMethod(String name){
		for (Iterator<QCodeBase> i = codes.iterator(); i
				.hasNext();) {
			QCodeBase c = i.next();
			if(c instanceof QMethod && ((QMethod)c).name.equals(name)){
				i.remove();
			}
			
		}
	}
	
	public void visit(QLangBase base) {
		if(constructor != null){
			base.accept(constructor);
		}
		
		for (Iterator<QVarDef> i = variables.iterator(); i
				.hasNext();) {
			QVarDef v = i.next();
			base.accept(v);
		}
		
		super.visit(base);
	}

	public void extend(String extend) {
		this.extend = extend;
	}
}

package org.qrone.coder.render;

import org.qrone.coder.QArray;
import org.qrone.coder.QBlock;
import org.qrone.coder.QCall;
import org.qrone.coder.QChoice;
import org.qrone.coder.QClass;
import org.qrone.coder.QClause;
import org.qrone.coder.QCode;
import org.qrone.coder.QCodeBase;
import org.qrone.coder.QFunc;
import org.qrone.coder.QHash;
import org.qrone.coder.QMethod;
import org.qrone.coder.QNumber;
import org.qrone.coder.QOp;
import org.qrone.coder.QState;
import org.qrone.coder.QStr;
import org.qrone.coder.QVar;
import org.qrone.coder.QVarDef;

public abstract class QLangBase {
	private int indentcount = 0;
	private StringBuilder b = new StringBuilder();
	
	public String build(){
		return b.toString();
	}
	
	public void write(char c){
		b.append(c);
	}
	
	public void write(String str){
		b.append(str);
	}
	
	public void indent(){
		indentcount++;
	}
	
	public void outdent(){
		indentcount--;
	}
	
	public void br(){
		b.append("\n");
		for (int i = 0; i < indentcount; i++) {
			b.append("\t");
		}
	}

	public void accept(QCodeBase code){
		if(code instanceof QMethod){
			accept((QMethod)code);
		}else if(code instanceof QArray){
			accept((QArray)code);
		}else if(code instanceof QHash){
			accept((QHash)code);
		}else if(code instanceof QCall){
			accept((QCall)code);
		}else if(code instanceof QClass){
			accept((QClass)code);
		}else if(code instanceof QClause){
			accept((QClause)code);
		}else if(code instanceof QFunc){
			accept((QFunc)code);
		}else if(code instanceof QOp){
			accept((QOp)code);
		}else if(code instanceof QStr){
			accept((QStr)code);
		}else if(code instanceof QState){
			accept((QState)code);
		}else if(code instanceof QVar){
			accept((QVar)code);
		}else if(code instanceof QCode){
			accept((QCode)code);
		}else if(code instanceof QVarDef){
			accept((QVarDef)code);
		}else if(code instanceof QBlock){
			accept((QBlock)code);
		}else if(code instanceof QChoice){
			accept((QChoice)code);
		}
	}
	public abstract void accept(QArray cls);
	public abstract void accept(QHash cls);
	public abstract void accept(QBlock cls);
	public abstract void accept(QCall cls);
	public abstract void accept(QClass cls);
	public abstract void accept(QClause cls);
	public abstract void accept(QFunc cls);
	public abstract void accept(QMethod cls);
	public abstract void accept(QState cls);
	public abstract void accept(QVar cls);
	public abstract void accept(QVarDef cls);
	public void accept(QChoice cls){
		write("(");
		cls.cond.visit(this);
		write(" ? ");
		cls.yes.visit(this);
		write(" : ");
		cls.no.visit(this);
		write(")");
	}

	public void accept(QCode cls) {
		write(cls.code);
	}
	
	public void accept(String str, boolean quot) {
		if(quot){
			write('"');
			write(str
					.replaceAll("\"", "\\\\\\\"")
					.replaceAll("\n", " ")
					.replaceAll("\r", "")
					.replaceAll("\t", " ")
					);
			write('"');
		}else{
			write(str);
		}
	}

	public void accept(QOp qOp) {
		write(qOp.op);
	}

	public void accept(QNumber o) {
		write(o.number.toString());
	}

	public void accept(QStr o) {
		accept(o.str.toString(), true);
	}

	
}

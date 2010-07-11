package org.qrone.coder.render;

import java.util.Iterator;

import org.qrone.coder.QArray;
import org.qrone.coder.QBlock;
import org.qrone.coder.QCall;
import org.qrone.coder.QClass;
import org.qrone.coder.QClause;
import org.qrone.coder.QCodeBase;
import org.qrone.coder.QFunc;
import org.qrone.coder.QHash;
import org.qrone.coder.QMethod;
import org.qrone.coder.QOp;
import org.qrone.coder.QState;
import org.qrone.coder.QVar;
import org.qrone.coder.QVarDef;

public class QLangPHP extends QLangBase{

	@Override
	public void accept(QClass cls) {
		br();
		write("public class ");
		write(cls.className);
		if(cls.extend != null)
			write(" extends " + cls.extend);
		write("{");
		indent();
		cls.visit(this);
		outdent();
		br();
		write("}");
	}

	@Override
	public void accept(QVar var) {
		if(var.ref){
			write(var.name);
		}else{
			write("$");
			write(var.name);
		}
	}

	@Override
	public void accept(QFunc func) {
		br();
		write("public function ");
		write("__construct");
		write("(");
		
		boolean first = true;
		for (Iterator<QVar> i = func.args.iterator(); i
				.hasNext();) {
			QVar v = i.next();

			if(!first) write(", ");
			accept(v);
			
			if(first) first = false;
		}
		write(")");
		
		write("{");
		indent();
		func.visit(this);
		outdent();
		br();
		write("}");
	}

	@Override
	public void accept(QMethod func) {
		br();
		write("public function ");
		write(func.name);
		write("(");
		
		boolean first = true;
		for (Iterator<QVar> i = func.args.iterator(); i
				.hasNext();) {
			QVar v = i.next();

			if(!first) write(", ");
			accept(v);
			
			if(first) first = false;
		}
		write(")");
		
		write("{");
		indent();
		func.visit(this);
		outdent();
		br();
		write("}");
	}
	
	@Override
	public void accept(QState cls) {
		br();
		cls.visit(this);
		write(";");
	}

	@Override
	public void accept(QCall o) {
		write(o.name);
		write("(");
		boolean first = true;
		for (Iterator<QState> i = o.args.iterator(); i
				.hasNext();) {
			QState v = i.next();

			if(!first) write(", ");
			v.visit(this);
			
			if(first) first = false;
		}
		write(")");
	}

	@Override
	public void accept(QVarDef def) {
		br();
		write("public var ");
		write(def.var.name);
		if(def.state != null){
			write(" = ");
			def.state.visit(this);
		}
		write(";");
	}

	@Override
	public void accept(QBlock cls) {
		for (Iterator<QCodeBase> i = cls.codes.iterator(); i
				.hasNext();) {
			QCodeBase v = i.next();
			br();
			v.visit(this);
			write(";");
		}
	}

	@Override
	public void accept(QClause cls) {
		write(cls.type + "(");
		boolean first = true;
		for (Iterator<QState> i = cls.args.iterator(); i
				.hasNext();) {
			QState v = i.next();

			if(!first) write(", ");
			v.visit(this);
			
			if(first) first = false;
		}
		write("){");
		indent();
		for (Iterator<QCodeBase> i = cls.codes.iterator(); i
				.hasNext();) {
			QCodeBase v = i.next();
			br();
			v.visit(this);
			write(";");
		}
		outdent();
		br();
		write("}");
	}

	@Override
	public void accept(QOp qOp) {
		if(qOp.op.equals("->")){
			write("->");
		}else if(qOp.op.equals("[")){
			write("[");
		}else if(qOp.op.equals("]")){
			write("]");
		}else{
			write(" ");
			write(qOp.op);
			write(" ");
		}
	}
	@Override
	public void accept(QArray cls) {
		write("array(");
		
		boolean first = true;
		for (Iterator<QState> i = cls.array.iterator(); i
				.hasNext();) {
			QState v = i.next();

			if(!first) write(", ");
			v.visit(this);
			
			if(first) first = false;
		}
		write(")");
	}

	@Override
	public void accept(QHash cls) {
		write("array(");
		
		boolean first = true;
		for (Iterator<QState> i = cls.hash.keySet().iterator(); i
				.hasNext();) {
			QState key = i.next();

			if(!first) write(", ");
			key.visit(this);
			write(" => ");
			cls.hash.get(key).visit(this);
			
			if(first) first = false;
		}
		write(")");
	}
}

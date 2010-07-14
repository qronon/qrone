package org.qrone.r7;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.qrone.coder.QClass;
import org.qrone.coder.QFunc;
import org.qrone.coder.QState;
import org.qrone.coder.render.QLangJQuery;
import org.qrone.r7.handler.ImageHandler;
import org.qrone.r7.handler.Scale9Handler;
import org.qrone.r7.parser.CSS3Parser;
import org.qrone.r7.parser.Delegate;
import org.qrone.r7.parser.HTML5OM;
import org.qrone.r7.parser.HTML5Selializer;
import org.qrone.r7.parser.HTML5Template;
import org.qrone.r7.parser.HTML5Writer;
import org.qrone.r7.parser.JSParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import fmpp.util.FileUtil;

public class XOM extends HTML5OM{
	private File file;
	
	public XOM() {
		addTagHandler(new Scale9Handler(this));
		addTagHandler(new ImageHandler(this));
	}
	
	public File getFile(){
		return file;
	}
	
	public void parse(File f) throws FileNotFoundException, SAXException, IOException{
		this.file = f;
		
		String className = file.getName();
		if(className.indexOf('.') >= 0){
			className = className.substring(0, className.indexOf('.'));
		}
		
		parse(className, new FileReader(file));
	}

	public void process(HTML5Writer t){
		process(t, false);
	}

	public void process(HTML5Writer t, boolean bodyOnly){
		process(t, bodyOnly, null, null);
	}
	
	public void process(final HTML5Writer t, final boolean bodyOnly, 
			final Stack<XOM> xomlist, final String target){
		if(!bodyOnly){
			String path = getMETAMap().get("include-in");
			if(path != null){
				String[] paths = path.split("#", 2);
				if(paths.length == 2){
					try {
						File file = FileUtil.resolveRelativeUnixPath(
								XCompiler.root, getFile().getParentFile(), paths[0]);
						XOM xom = XCompiler.compile(file);
						if(xom != null){
							Stack<XOM> xoml = xomlist;
							if(xomlist == null){
								xoml = new Stack<XOM>();
							}
							xoml.push(this);
							xom.process(t, false, xoml, paths[1]);
							return;
						}
					} catch (IOException e) {
					}
				}
			}
		}
		
		HTML5Selializer s = new HTML5Selializer() {
			int formatting = 0;
			boolean inBody;
			boolean inScript;
			
			@Override
			public void visit(Document e) {
				out("<!DOCTYPE html>");
				super.visit(e);
			}
			
			@Override
			public void visit(Element e) {
				if(e.getNodeName().equals("head")){
					start(e);
					accept(e);
					out(XCompiler.getRecurseHeader(b, file, xomlist));
					end(e);
				}else if(e.getNodeName().equals("body")){
					if(!bodyOnly){
						start(e);
					}
					inBody = true;
					accept(e);
					inBody = false;
					if(!bodyOnly){
						end(e);
					}
				}else if(e.getNodeName().equals("script")){
					if(inBody){
						start(e);
						inScript = true;
						accept(e);
						inScript = false;
						end(e);
					}
				}else if(e.getNodeName().equals("style")){
				}else if(e.getNodeName().equals("link")){
				}else if(e.getNodeName().equals("pre") || e.getNodeName().equals("code")){
					formatting++;
					out(e);
					formatting--;
				}else if(e.getNodeName().equals("meta")){
					if(e.getAttribute("name").equals("extends")){
					}else{
						start(e);
						accept(e);
						end(e);
					}
				}else{
					out(e);
				}
			}

			@Override
			public void visit(Text n) {
				if(inScript){
					out(jsmin(n.getNodeValue(), "qrone." + getClassName()));
				}else if(formatting>0){
					writeraw(n.getNodeValue());
				}else if(inBody){
					write(n.getNodeValue());
				}
			}
			
			@Override
			protected void out(Element e) {
				final String include = getProperty(e, "include");
				if(include != null){
					final String str = CSS3Parser.pullstring(include);
					if(str != null && str.trim().length() > 0){
						super.out(e,new Delegate() {
							@Override
							public void accept() {
								XOM xom = XCompiler.compile(file, str);
								xom.process(t, true);
							}
						});
					}else{
						try{
							throw new IOException();
						}catch(IOException e1){
							e1.printStackTrace();
						}
					}
				}else if(target != null && e.getAttribute("id").equals(target)){
					super.out(e,new Delegate() {
						@Override
						public void accept() {
							XOM xom = xomlist.pop();
							xom.process(t, true);
						}
					});
				}else{
					super.out(e);
				}
			}
		};
		s.visit(this, bodyOnly ? body : document.getDocumentElement(), t);
	}

	public String getHTML(String id){
		HTML5Template t = new HTML5Template();
		process(t);
		t.setValue("id", id);
		return t.toString();
	}
	
	public String getHTML(){
		return getHTML("");
	}

	public String getScripts(boolean html){

		final QClass jqueryclass = new QClass(getClassName());
		final QFunc method = jqueryclass.constructor();
		method.arg("String", "id");
		final QState jqueryhtml = method.state().returns();
		
		process(new HTML5Writer() {
			@Override
			public void append(String key, String value) {
				jqueryhtml.var("String", key);
			}
			
			@Override
			public void append(String str) {
				jqueryhtml.str(str);
			}
			
			@Override
			public void append(char c) {
				jqueryhtml.str(String.valueOf(c));
			}
		}, true);
		
		StringBuilder b = new StringBuilder();
		if(!html){
			b.append("qrone." + getClassName() + "=function(){};");
		}else{
			QLangJQuery q = new QLangJQuery();
			q.accept(jqueryclass);
			b.append(q.build());
		}
		
		for (Iterator<String> i = javascripts.iterator(); i
				.hasNext();) {
			String userjs = i.next();
			b.append(JSParser.compress(userjs.toString(), true)
				.replace("__QRONE_PREFIX_NAME__","qrone." + getClassName()));
		}
		
		return b.toString();
	}
	
	public String getScripts() {
		return getScripts(true);
	}
	
	public String serialize(){
		return serialize(null);
	}
	
	public String serialize(String lang){
		if(lang != null && lang.equals("js")){
			return getScripts(true);
		}else{
			return getHTML("");
		}
	}
}

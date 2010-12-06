package org.qrone.r7.tag;

import org.qrone.r7.parser.CSS3Value;
import org.qrone.r7.parser.HTML5Element;
import org.qrone.r7.parser.HTML5OM;
import org.qrone.r7.parser.HTML5Template;
import org.qrone.r7.parser.NodeProcessor;
import org.qrone.r7.script.browser.Function;
import org.w3c.dom.Element;

public class CSSIncludeInHandler implements HTML5TagHandler{

	@Override
	public HTML5TagResult process(final HTML5Element e) {
		final CSS3Value path = e.getPropertyValue("include-in");
		if(path != null){
			final String[] paths = path.getURL().split("#", 2);
			if(paths.length == 2){
				return new HTML5TagResult() {
					
					@Override
					public String prestart() {
						return null;
					}
					
					@Override
					public String preend() {
						return null;
					}
					
					@Override
					public String poststart() {
						return null;
					}
					
					@Override
					public String postend() {
						return null;
					}

					@Override
					public void process(final HTML5Element e) {
						final HTML5OM xom = e.getOM().getDeck().compile(e.getOM().getURI().resolve(paths[0]));
						if(xom != null){
							
							e.html(new Function() {
								
								@Override
								public Object call(Object... args) {
									xom.process(t, p, node, id, xomlist)
									final NodeProcessor t = (NodeProcessor)args[2];
									t.
									t.out(xom, new NodeProcessor() {

										@Override
										public HTML5Element get(Element node) {
											String id = node.getAttribute("id");
											if(id != null && id.equals(paths[1])){
												final HTML5Element e = t.get(node);
												e.html(new Function(){
													@Override
													public Object call(Object... args) {
														HTML5Template t1 = t.newTemplate();
														t1.out(e);
														return t1;
													}
												});
											}
											return e;
										}

										@Override
										public void visit(HTML5Element e) {
											t.visit(e);
										}

										@Override
										public void out(HTML5OM o) {
											t.out(o);
										}

										@Override
										public void append(String string) {
											t.append(string);
										}
									});
									return xom;
								}
							});
						}
					}
				};
				
				
			}
		}
		return null;
	}

}

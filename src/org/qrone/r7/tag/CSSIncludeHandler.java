package org.qrone.r7.tag;

import java.io.IOException;

import org.qrone.r7.parser.CSS3Value;
import org.qrone.r7.parser.HTML5Element;

public class CSSIncludeHandler implements HTML5TagHandler{

	@Override
	public HTML5TagResult process(final HTML5Element e) {
		final CSS3Value include = e.getPropertyValue("include");
		if(include != null){
			final String path = include.getURL();
			if(path != null && path.trim().length() > 0){
				
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
					public void process(HTML5Element e) {
						e.html(e.getOM().getDeck().compile(e.getOM().getURI().resolve(path)));
					}
				};
				
				
			}else{
				try{
					throw new IOException();
				}catch(IOException e2){
					e2.printStackTrace();
				}
			}
		}
		return null;
	}

}

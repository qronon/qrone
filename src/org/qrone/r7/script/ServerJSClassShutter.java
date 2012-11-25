package org.qrone.r7.script;

import org.mozilla.javascript.ClassShutter;

public class ServerJSClassShutter implements ClassShutter{

	public static final String[] denyClasses = {
		"java.lang.System",
		"java.lang.Thread",
		"java.lang.ThreadGroup",
		"java.lang.ThreadLocal",
	};
	
	public static final String[] allowPackages = {
		"java.lang",
		"java.math",
		"java.util",
		"org.w3c.dom",
		"org.qrone.database",
		"org.qrone.img",
		"org.qrone.kvs",
		"org.qrone.login",
		"org.qrone.memcached",
		"org.qrone.mongo",
		"org.qrone.r7.fetcher",
		"org.qrone.r7.format",
		"org.qrone.r7.parser",
		"org.qrone.r7.script",
		"org.qrone.r7.script.browser",
		"org.qrone.r7.script.ext",
		"org.qrone.r7.script.tag",
		"org.qrone.r7.parser",
		"org.qrone.util",
		"org.eclipse.jetty.server",
	};

	@Override
	public boolean visibleToScripts(String fullClass) {
		for (int i = 0; i < denyClasses.length; i++) {
			if(fullClass.equals(denyClasses[i])){
				return false;
			}
		}
		
		for (int i = 0; i < allowPackages.length; i++) {
			if(fullClass.substring(0, fullClass.lastIndexOf('.')).equals(allowPackages[i])){
				return true;
			}
		}
		return false;
	}

}

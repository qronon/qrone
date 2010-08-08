package org.qrone.r7;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletContext;

import org.qrone.r7.resolver.URIResolver;
import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;
import org.scannotation.WarUrlFinder;

public class ExtensionIndex {
	private AnnotationDB db;
	private Set<String> extClasses;
	private String psprite = "/extension-pack.ser";
	private URI pspriteURI;
	
	public ExtensionIndex() {
		db = new AnnotationDB();
		try {
			pspriteURI = new URI(psprite);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public void find(ServletContext context){
		try {
			db.scanArchives(WarUrlFinder.findWebInfLibClasspaths(context));
			db.scanArchives(WarUrlFinder.findWebInfClassesPath(context));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void find() {
		try {
			db.scanArchives(ClasspathUrlFinder.findClassPaths());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void extend(Extendable e){
		Set<String> classes = extClasses;
		if(classes == null)
			classes = db.getAnnotationIndex().get(Extension.class.getName());
		if(classes != null){
			for (Iterator<String> i = classes.iterator(); i
					.hasNext();) {
				try {
					e.addExtension(Class.forName(i.next()));
				} catch (ClassNotFoundException e1) {}
			}
		}
	}

	public boolean pack(URIResolver resolver) {
		Set<String> classes = db.getAnnotationIndex().get(Extension.class.getName());
		if(classes != null){
			HashSet<String> set = new HashSet<String>();
			set.addAll(classes);
			
			try {
				QrONEUtils.serialize(set, resolver.getOutputStream(pspriteURI));
				return true;
			} catch (IOException e) {
			}
		}
		return false;
	}
	
	public Set<String> unpack(URIResolver resolver) {
		try {
			extClasses = (Set<String>)QrONEUtils.unserialize(resolver.getInputStream(pspriteURI));
		} catch (IOException e) {
		}
		return extClasses;
	}
}

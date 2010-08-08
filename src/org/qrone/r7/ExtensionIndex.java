package org.qrone.r7;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
		for (Iterator<String> i = classes.iterator(); i
				.hasNext();) {
			try {
				e.addExtension(Class.forName(i.next()));
			} catch (ClassNotFoundException e1) {}
		}
	}

	public boolean pack(URIResolver resolver) {
		Set<String> classes = db.getAnnotationIndex().get(Extension.class.getName());
		HashSet<String> set = new HashSet<String>();
		set.addAll(classes);
		
		ObjectOutputStream out = null;
		try {
			
			out = new ObjectOutputStream(resolver.getOutputStream(pspriteURI));
			out.writeObject(set);
			out.flush();
			return true;
		} catch (InvalidClassException e) {
		} catch (NotSerializableException e) {
		} catch (IOException e) {
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
		return false;
	}
	
	public Set<String> unpack(URIResolver resolver) {
		if (extClasses == null && pspriteURI != null) {
			ObjectInputStream oin = null;
			try {
				InputStream in = resolver.getInputStream(pspriteURI);
				if (in != null) {
					oin = new ObjectInputStream(in);
					extClasses = (HashSet<String>) oin.readObject();
				}
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
			} finally {
				if (oin != null) {
					try {
						oin.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return extClasses;
	}
}

/*
 * YUI Compressor
 * Author: Julien Lecomte <jlecomte@yahoo-inc.com>
 * Copyright (c) 2007, Yahoo! Inc. All rights reserved.
 * Code licensed under the BSD License:
 *     http://developer.yahoo.net/yui/license.txt
 */

package org.qrone.r7;

import jargs.gnu.CmdLineParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import org.qrone.r7.handler.ImageHandler;
import org.qrone.r7.handler.Scale9Handler;
import org.qrone.r7.parser.HTML5Deck;
import org.qrone.r7.parser.HTML5OM;



public class QrONECompressor {
	
	public static HTML5Deck deck;
	public static boolean verbose;
	

    public static void main(String args[]) {
    	long timer = System.currentTimeMillis();

        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option helpOpt     = parser.addBooleanOption('h', "help");
        CmdLineParser.Option languageOpt = parser.addStringOption('l', "lang");
        CmdLineParser.Option recurseOpt  = parser.addBooleanOption('r', "recurse");
        CmdLineParser.Option imagedirOpt = parser.addStringOption('i', "img-basedir");
        CmdLineParser.Option noImagesOpt = parser.addBooleanOption('n', "noimages");
        CmdLineParser.Option charsetOpt  = parser.addStringOption("charset");
        CmdLineParser.Option verboseOpt  = parser.addBooleanOption('v', "verbose");

        try {
			parser.parse(args);
		} catch (CmdLineParser.OptionException e) {
            usage();
            System.exit(1);
        }
		
        Boolean help = (Boolean) parser.getOptionValue(helpOpt);
        if (help != null && help.booleanValue()) {
            usage();
            System.exit(0);
        }
        
        boolean noimages = parser.getOptionValue(noImagesOpt) != null;

        verbose = parser.getOptionValue(verboseOpt) != null;

        String charset = (String) parser.getOptionValue(charsetOpt);
        if (charset == null || !Charset.isSupported(charset)) {
            //charset = System.getProperty("file.encoding");
            if (charset == null) {
                charset = "UTF-8";
            }
            if (verbose) {
                System.err.println("[INFO] Using charset " + charset);
            }
        }
        
        String lang			  = (String) parser.getOptionValue(languageOpt);
        
        if(lang == null) lang = "html";
        else lang = lang.toLowerCase();
        
        /*
        Writer out = null;
		try {
			out = new OutputStreamWriter(System.out, charset);
		} catch (UnsupportedEncodingException e) {
            System.err.println("[ERROR] Using charset " + charset);
            System.exit(0);
		}
        */
        String imgdir = (String) parser.getOptionValue(imagedirOpt);

        boolean recurse = parser.getOptionValue(recurseOpt) != null;

        String[] fileArgs = parser.getRemainingArgs();
        if(fileArgs.length < 1){
            usage();
            System.exit(0);
        }
        
        File target = new File(fileArgs[0]).getAbsoluteFile();
        File basedir;
        String path;
        if(target.isDirectory()){
        	basedir = target;
        	path = "";
        }else{
        	basedir = target.getParentFile();
        	path = target.getName();
        }
        
        URI imgbaseuri = null;
        try {
	    	if(imgdir != null){
					imgbaseuri = new URI(fileArgs[0]).relativize(new URI(imgdir));
	    	}else{
	    		imgbaseuri = new URI(".");
	    	}
	    	
		} catch (URISyntaxException e) {
			e.printStackTrace();
            System.exit(0);
		}

        if(deck == null)
        	deck = new HTML5Deck(new FileURIResolver(basedir));
    	deck.getSpriter().setImageDir(imgbaseuri);
    	
    	deck.addTagHandler(new Scale9Handler(deck));
    	deck.addTagHandler(new ImageHandler(deck));
        
        
        compile(deck, target, path, lang, recurse);
        
		if(!noimages){
			if (verbose) {
	            System.err.println("[INFO] Writing sprite images.");
	        }
			
			try {
				deck.getSpriter().create();
			} catch (IOException e) {
	            System.err.println("[ERROR] Creating sprite images.");
	            System.exit(0);
			}
			
			if (verbose) {
	            System.err.println("[INFO] Writing sprite images done.");
	        }
		}
		
		if (verbose) {
	    	timer = System.currentTimeMillis() - timer;
            System.err.println("\n[INFO] Compilation time " + timer + "ms");
        }
    }
    
	public static void compile(HTML5Deck deck, File file, String path, String lang, boolean recurse){
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if( files[i].isDirectory() && recurse)
					compile(deck, files[i], files[i].getName(), lang, recurse);
				else if( files[i].getName().indexOf("-min.") < 0 &&
						(files[i].getName().endsWith(".html") || files[i].getName().endsWith(".htm")) )
					compile(deck, files[i], files[i].getName(), lang, recurse);
			}
		}else{
	        if(lang == null) lang = "html";
	        else lang = lang.toLowerCase();
	        
			String basename = file.getName().substring(0, 
					file.getName().indexOf('.'));
			File outfile = new File(file.getParentFile(),basename + "-min." + lang);
		
			
			FileWriter out = null;
			try {
				out = new FileWriter(outfile);
			} catch (IOException e1) {
		        System.err.println("[ERROR] Opening file " + outfile.getName());
		        System.exit(0);
			}
			
		    try {
				if (verbose) {
		            System.err.println("[INFO] Parsing " + file.getName());
		        }
				
				HTML5OM xom = deck.compile(new URI(path));
				out.write(xom.serialize(lang));
				//xom.serialize(lang);
				if (verbose) {
		            System.err.println("[INFO] Writing " + outfile.getName() + " done.");
		        }
			} catch (IOException e) {
				e.printStackTrace();
		        System.err.println("[ERROR] Parsing file " + file.getName());
		        System.exit(0);
			} catch (URISyntaxException e) {
				e.printStackTrace();
		        System.err.println("[ERROR] Parsing file " + file.getName());
		        System.exit(0);
			} finally {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	
    private static void usage() {
        System.out.println(
                "\nUsage: java -jar qrone-x.y.z.jar [options] <file>\n\n"

                        + "Options\n"
                        + "  -h, --help                Displays this information\n"
                        + "  -v, --verbose             Display informational messages and warnings\n"
                        + "  --charset <charset>       Read the input file using <charset>\n"
                        + "\n"
                        + "  -l <language>             Output language, default is 'html'\n"
                        + "  -i, --img-basedir <dir>   CSS Sprite image directory\n"
                        + "  -u, --img-baseurl <url>   CSS Sprite image base url\n"
                        
                        );
    }
}

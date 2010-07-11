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
import java.nio.charset.Charset;

import fmpp.util.FileUtil;



public class QrONECompressor {
	
	public static boolean verbose;

    public static void main(String args[]) {
    	long timer = System.currentTimeMillis();

        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option helpOpt = parser.addBooleanOption('h', "help");
        CmdLineParser.Option languageOpt = parser.addStringOption('l', "lang");
        CmdLineParser.Option imagedirOpt = parser.addStringOption('i', "img-basedir");
        CmdLineParser.Option imageurlOpt = parser.addStringOption('u', "img-baseurl");
        CmdLineParser.Option noImagesOpt = parser.addBooleanOption('n', "noimages");
        CmdLineParser.Option charsetOpt = parser.addStringOption("charset");
        CmdLineParser.Option verboseOpt = parser.addBooleanOption('v', "verbose");

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
        String imgurl = (String) parser.getOptionValue(imageurlOpt);
        String imgdir = (String) parser.getOptionValue(imagedirOpt);

        String[] fileArgs = parser.getRemainingArgs();
        if(fileArgs.length < 1){
            usage();
            System.exit(0);
        }
        
        File target = new File(fileArgs[0]).getAbsoluteFile();
        if(target.isDirectory()){
        	XCompiler.root = target;
        	if(imgdir != null){
        		try {
					target = FileUtil.resolveRelativeUnixPath(XCompiler.root, XCompiler.root, imgdir);
				} catch (IOException e) {
					e.printStackTrace();
		            System.exit(0);
				}
        	}
        	ImageSpriter.instance().setImageDir(target, imgurl);
        }else{
        	XCompiler.root = target.getParentFile();
        	if(imgdir != null){
        		try {
					target = FileUtil.resolveRelativeUnixPath(XCompiler.root, XCompiler.root, imgdir);
				} catch (IOException e) {
					e.printStackTrace();
		            System.exit(0);
				}
        	}
        	ImageSpriter.instance().setImageDir(target.getParentFile(), imgdir);
        }
        
        compile(new File(fileArgs[0]), lang);
        
		if(!noimages){
			if (verbose) {
	            System.err.println("[INFO] Writing sprite images.");
	        }
			
			try {
				ImageSpriter.instance().create();
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
    
	public static void compile(File infile, String lang){
		if(infile.isDirectory()){
			File[] files = infile.listFiles();
			for (int i = 0; i < files.length; i++) {
				if( files[i].getName().indexOf("-min.") < 0 &&
						(files[i].getName().endsWith(".html") || files[i].getName().endsWith(".htm")) )
					 compile(files[i], lang);
			}
		}else{
	        if(lang == null) lang = "html";
	        else lang = lang.toLowerCase();
	        
			String basename = infile.getName().substring(0, 
					infile.getName().indexOf('.'));
			File outfile = new File(infile.getParentFile(),basename + "-min." + lang);
		
			
			FileWriter out = null;
			try {
				out = new FileWriter(outfile);
			} catch (IOException e1) {
		        System.err.println("[ERROR] Opening file " + outfile.getName());
		        System.exit(0);
			}
			
		    try {
				if (verbose) {
		            System.err.println("[INFO] Parsing " + infile.getName());
		        }
				
				XOM xom = XCompiler.compile(infile);
				out.write(xom.serialize(lang));
				if (verbose) {
		            System.err.println("[INFO] Writing " + outfile.getName() + " done.");
		        }
			} catch (IOException e) {
				e.printStackTrace();
		        System.err.println("[ERROR] Parsing file " + infile.getName());
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

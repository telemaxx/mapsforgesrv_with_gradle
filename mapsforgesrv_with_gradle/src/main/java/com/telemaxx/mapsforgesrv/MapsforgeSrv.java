package com.telemaxx.mapsforgesrv;
import java.io.File;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import org.apache.commons.cli.*;
import org.eclipse.jetty.server.Server;

public class MapsforgeSrv {

	public static void main(String[] args) throws Exception {
		System.out.println("MapsforgeSrv - a mapsforge tile server");

		String[] mapFilePaths = null;
		String themeFilePath = null;
		String themeFileStyle = null;
		String preferredLanguage = null;
		final int DEFAULTPORT = 8080; 
		String portNumberString = "" + DEFAULTPORT;


		Options options = new Options();

		Option mapfileArgument = new Option("m", "mapfiles", true, "comma-separated list of mapsforge map files (.map)");
		mapfileArgument.setRequired(true);
		options.addOption(mapfileArgument);

		Option themefileArgument = new Option("t", "themefile", true, "mapsforge theme file(.xml), (default: the internal OSMARENDER)");
		themefileArgument.setRequired(false);
		options.addOption(themefileArgument);
		
		Option themefileStyleArgument = new Option("s", "style", true, "style from theme file(.xml), (default: default defined in xml file)");
		themefileStyleArgument.setRequired(false);
		options.addOption(themefileStyleArgument);		

		Option languageArgument = new Option("l", "language", true, "preferred language (default: native language)");
		languageArgument.setRequired(false);
		options.addOption(languageArgument);

		Option portArgument = new Option("p", "port", true, "port, where the server is listening(default: " + DEFAULTPORT + ")");
		portArgument.setRequired(false);
		options.addOption(portArgument);

		Option interfaceArgument = new Option("if", "interface", true, "which interface listening [all,localhost] (default: localhost)");
		interfaceArgument.setRequired(false);
		options.addOption(interfaceArgument);        

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;      

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("mapsforgesrv", options);
			System.exit(1);
		}

		int portNumber = DEFAULTPORT;
		portNumberString = cmd.getOptionValue("port");
		if (portNumberString != null) {
			try {
				portNumberString = portNumberString.trim();
				//System.out.println("portString" + portNumberString);
				portNumber = Integer.parseInt(portNumberString);
				if (portNumber < 1024 || portNumber > 65535) {
					portNumber = DEFAULTPORT;
					System.out.println("portnumber not 1024-65535, exit");
					System.exit(1);
				} else {
					System.out.println("using port: " + portNumber);
				}
			} catch (NumberFormatException e){
				portNumber = DEFAULTPORT;
				System.out.println("couldnt parse portnumber, using " + DEFAULTPORT);
				//e.printStackTrace();
			}
		} else {
			System.out.println("no port given, using " + DEFAULTPORT);
		}

		mapFilePaths = cmd.getOptionValue("mapfiles").trim().split(",");
		ArrayList<File> mapFiles = new ArrayList<>();
		for (String path : mapFilePaths) {
			mapFiles.add(new File(path));
		}
		mapFiles.forEach(mapFile -> {
			System.out.println("Map file: " + mapFile);
			if (!mapFile.isFile()) {
				System.err.println("ERROR: Map file does not exist!");
				System.exit(1);
			}
		});

		themeFilePath = cmd.getOptionValue("themefile");
		if (themeFilePath != null) {
			themeFilePath = themeFilePath.trim();
		}
		File themeFile = null;
		if (themeFilePath != null) {	
			themeFile = new File(themeFilePath);
			System.out.println("Theme file: " + themeFile);
			if (!themeFile.isFile()) {
				System.err.println("ERROR: theme file does not exist!");
				System.exit(1);
			}
		} else {
			System.out.println("Theme: OSMARENDER");
		}
		
		themeFileStyle = cmd.getOptionValue("style");
		if (themeFileStyle != null) {
			themeFileStyle = themeFileStyle.trim();
			System.out.println("selected ThemeStyle: " + themeFileStyle);
		}	
		
		preferredLanguage = cmd.getOptionValue("language");
		System.out.println("preferredLanguage, using " + preferredLanguage);
		
		MapsforgeHandler mapsforgeHandler = new MapsforgeHandler(mapFiles, themeFile, themeFileStyle, preferredLanguage);

		Server server = null;
		String listeningInterface = cmd.getOptionValue("interface");
		if (listeningInterface != null) {
			listeningInterface = listeningInterface.trim();
			if (listeningInterface.toLowerCase().equals("all")) {
				System.out.println("listening on all interfaces, port:" + portNumber);
				server = new Server(portNumber);
			} else if (listeningInterface.toLowerCase().equals("localhost")) {
				//listeningInterface = "localhost";
				System.out.println("listening on localhost port:" + portNumber);
				server = new Server(InetSocketAddress.createUnresolved("localhost", portNumber));
			} else {
				System.out.println("unkown Interface, only \"all\" or \"localhost\" , not " + listeningInterface );
				System.exit(1);	
			}
		} else {
			System.out.println("listening on localhost port:" + portNumber);
			server = new Server(InetSocketAddress.createUnresolved("localhost", portNumber));
		}

		server.setHandler(mapsforgeHandler);
		try {
			server.start();
		} catch (BindException e) {
			System.out.println(e.getMessage());
			System.out.println("Stopping server");
			System.exit(1);
		}
		server.join();
	}

}
package com.telemaxx.mapsforgesrv;
import java.io.File;
import java.net.BindException;
import java.net.InetSocketAddress;
import org.apache.commons.cli.*;
import org.eclipse.jetty.server.Server;

public class MapsforgeSrv {

	public static void main(String[] args) throws Exception {
		System.out.println("MapsforgeSrv - a mapsforge tile server");
		
		String mapFilePath = null;
		String themeFilePath = null;
		
		Options options = new Options();
		Option mapfileArgument = new Option("m", "mapfile", true, "mapsforge map file(.map)");
	    mapfileArgument.setRequired(true);
	    options.addOption(mapfileArgument);
		
        Option themefileArgument = new Option("t", "themefile", true, "mapsforge theme file(.xml)");
        themefileArgument.setRequired(false);
        options.addOption(themefileArgument);	
        
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
        
        mapFilePath = cmd.getOptionValue("mapfile").trim();
        
        themeFilePath = cmd.getOptionValue("themefile");
        if (themeFilePath != null) {
        	themeFilePath = themeFilePath.trim();
        }
        
        File mapFile = new File(mapFilePath);
		System.out.println("Map file: " + mapFile);
		
		if (!mapFile.isFile()) {
			System.err.println("ERROR: Map file does not exist!");
			System.exit(1);
		}

		File themeFile = null;
		if (themeFilePath != null) {	
			themeFile = new File(themeFilePath);
			System.out.println("Render theme file: " + themeFile);
			if (!themeFile.isFile()) {
				System.err.println("ERROR: Render theme file does not exist!");
				System.exit(1);
			}
		} else {
			System.out.println("Theme: OSMARENDER");
		}

		MapsforgeHandler mapsforgeHandler = new MapsforgeHandler(mapFile, themeFile);

		Server server = new Server(InetSocketAddress.createUnresolved("localhost", 8080));
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